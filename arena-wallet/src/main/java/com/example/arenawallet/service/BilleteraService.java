package com.example.arenawallet.service;

import com.example.arenawallet.client.UserClient;
import com.example.arenawallet.client.UserResponse;
import com.example.arenawallet.dto.BilleteraCommand;
import com.example.arenawallet.dto.BilleteraResult;
import com.example.arenawallet.exception.BilleteraNotFoundException;
import com.example.arenawallet.model.Billetera;
import com.example.arenawallet.model.BilleteraHistory;
import com.example.arenawallet.repository.BilleteraHistoryRepository;
import com.example.arenawallet.repository.BilleteraRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class BilleteraService {

    private final BilleteraRepository billeteraRepository;
    private final BilleteraHistoryRepository historialRepository;
    private final UserClient userClient;

    public BilleteraService(BilleteraRepository billeteraRepository,
                            BilleteraHistoryRepository historialRepository,
                            UserClient userClient) {
        this.billeteraRepository = billeteraRepository;
        this.historialRepository = historialRepository;
        this.userClient = userClient;
    }

    public List<BilleteraResult> listarTodas() {
        return billeteraRepository.findAll().stream()
                .map(this::toResult)
                .toList();
    }

    public BilleteraResult obtenerPorId(Long id) {
        return toResult(buscarOFallar(id));
    }

    @Transactional
    public BilleteraResult crear(BilleteraCommand cmd) {
        // 1. Validar que el usuario existe en user-service antes de crear la billetera
        log.info("[WALLET] Verificando existencia de usuario id={} en user-service", cmd.idUsuario());
        UserResponse usuario = userClient.getUserById(cmd.idUsuario());

        if (usuario == null || usuario.id() == null) {
            log.warn("[WALLET] Usuario id={} no encontrado en user-service", cmd.idUsuario());
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "No existe un usuario con id " + cmd.idUsuario() + " en el sistema");
        }

        // 2. Validar que no exista ya una billetera para ese usuario
        if (billeteraRepository.existsByIdUsuario(cmd.idUsuario())) {
            throw new IllegalArgumentException(
                    "Ya existe una billetera para el usuario con ID " + cmd.idUsuario());
        }

        Billetera billetera = new Billetera();
        billetera.setIdUsuario(cmd.idUsuario());
        billetera.setSaldo(cmd.saldo() != null ? cmd.saldo() : 0.0);
        billetera.setPuntosFidelizacion(
                cmd.puntosFidelizacion() != null ? cmd.puntosFidelizacion() : 0);

        Billetera guardada = billeteraRepository.save(billetera);
        registrarHistorial(guardada, "CREACION", guardada.getSaldo(), 0.0, guardada.getSaldo());
        log.info("[WALLET] Billetera creada para usuario id={} (username={})", cmd.idUsuario(), usuario.username());
        return toResult(guardada);
    }

    @Transactional
    public BilleteraResult actualizar(Long id, BilleteraCommand cmd) {
        Billetera existente = buscarOFallar(id);

        if (!existente.getIdUsuario().equals(cmd.idUsuario())
                && billeteraRepository.existsByIdUsuarioAndIdNot(cmd.idUsuario(), id)) {
            throw new IllegalArgumentException(
                    "Ya existe una billetera para el usuario con ID " + cmd.idUsuario());
        }

        Double saldoAnterior = existente.getSaldo();
        existente.setIdUsuario(cmd.idUsuario());
        existente.setSaldo(cmd.saldo());
        existente.setPuntosFidelizacion(cmd.puntosFidelizacion());

        Billetera guardada = billeteraRepository.save(existente);
        registrarHistorial(guardada, "ACTUALIZACION", guardada.getSaldo(), saldoAnterior, guardada.getSaldo());
        return toResult(guardada);
    }

    @Transactional
    public BilleteraResult recargarSaldo(Long id, Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto de recarga debe ser un valor positivo");
        }
        Billetera billetera = buscarOFallar(id);
        Double saldoAnterior = billetera.getSaldo();

        billetera.setSaldo(saldoAnterior + monto);
        billetera.setPuntosFidelizacion(
                billetera.getPuntosFidelizacion() + (int) (monto / 1000));

        Billetera guardada = billeteraRepository.save(billetera);
        registrarHistorial(guardada, "RECARGA", monto, saldoAnterior, guardada.getSaldo());
        log.info("[WALLET] Recarga de ${} en billetera id={}. Nuevo saldo: ${}", monto, id, guardada.getSaldo());
        return toResult(guardada);
    }

    /**
     * Descuenta saldo de la billetera (llamado por session-service al finalizar sesión).
     * Lanza excepción si el saldo es insuficiente.
     */
    @Transactional
    public BilleteraResult descontarSaldo(Long id, Double monto) {
        if (monto == null || monto <= 0) {
            throw new IllegalArgumentException("El monto a descontar debe ser positivo");
        }
        Billetera billetera = buscarOFallar(id);
        Double saldoAnterior = billetera.getSaldo();

        if (saldoAnterior < monto) {
            log.warn("[WALLET] Saldo insuficiente en billetera id={}. Saldo: ${}, cobro: ${}", id, saldoAnterior, monto);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Saldo insuficiente. Saldo actual: $" + saldoAnterior + ", cobro requerido: $" + monto);
        }

        billetera.setSaldo(saldoAnterior - monto);
        Billetera guardada = billeteraRepository.save(billetera);
        registrarHistorial(guardada, "DESCUENTO_SESION", monto, saldoAnterior, guardada.getSaldo());
        log.info("[WALLET] Descuento de ${} en billetera id={}. Saldo anterior: ${}, nuevo: ${}",
                monto, id, saldoAnterior, guardada.getSaldo());
        return toResult(guardada);
    }

    public List<BilleteraHistory> obtenerHistorial(Long id) {
        if (!billeteraRepository.existsById(id)) {
            throw new BilleteraNotFoundException("Billetera con ID " + id + " no encontrada");
        }
        return historialRepository.findByBilleteraIdOrderByFechaOperacionDesc(id);
    }

    public void eliminar(Long id) {
        if (!billeteraRepository.existsById(id)) {
            throw new BilleteraNotFoundException("Billetera con ID " + id + " no encontrada");
        }
        billeteraRepository.deleteById(id);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────
    private Billetera buscarOFallar(Long id) {
        return billeteraRepository.findById(id)
                .orElseThrow(() -> new BilleteraNotFoundException(
                        "Billetera con ID " + id + " no encontrada"));
    }

    private BilleteraResult toResult(Billetera b) {
        return new BilleteraResult(
                b.getId(), b.getIdUsuario(), b.getSaldo(), b.getPuntosFidelizacion());
    }

    private void registrarHistorial(Billetera billetera, String tipo,
                                    Double monto, Double anterior, Double nuevo) {
        BilleteraHistory h = new BilleteraHistory();
        h.setBilletera(billetera);
        h.setTipoOperacion(tipo);
        h.setMontoInvolucrado(monto);
        h.setSaldoAnterior(anterior);
        h.setSaldoNuevo(nuevo);
        h.setFechaOperacion(LocalDateTime.now());
        historialRepository.save(h);
    }
}