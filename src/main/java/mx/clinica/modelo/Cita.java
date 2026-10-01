package mx.clinica.modelo;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Cita que relaciona un doctor y un paciente en una fecha y hora. */
public class Cita {

    public static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT);

    private String id;
    private LocalDateTime fechaHora;
    private String motivo;
    private Doctor doctor;
    private Paciente paciente;

    public Cita(String id, LocalDateTime fechaHora, String motivo, Doctor doctor, Paciente paciente) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.motivo = motivo;
        this.doctor = doctor;
        this.paciente = paciente;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Paciente getPaciente() {
        return paciente;
    }

    public void setPaciente(Paciente paciente) {
        this.paciente = paciente;
    }

    /** Devuelve la fecha con formato DD/MM/AAAA HH:MM. */
    public String getFechaHoraTexto() {
        return fechaHora.format(FORMATO_FECHA);
    }

    /** Devuelve los campos del CSV (doctor y paciente por su ID). */
    public String[] aCamposCsv() {
        return new String[]{id, getFechaHoraTexto(), motivo, doctor.getId(), paciente.getId()};
    }

    @Override
    public String toString() {
        return id + " | " + getFechaHoraTexto() + " | " + motivo
                + " | Dr: " + doctor.getId() + " - " + doctor.getNombre()
                + " | Pac: " + paciente.getId() + " - " + paciente.getNombre();
    }
}
