package br.com.siswbrasil;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import br.com.siswbrasil.model.CalendarEvent;
import br.com.siswbrasil.model.ExtendedProps;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

/**
 * Classe responsável por inicializar dados de exemplo na inicialização da aplicação.
 */
@ApplicationScoped
public class DataInitializer {

    @PersistenceContext
    EntityManager em;

    private static final List<String> TITLES = Arrays.asList(
            "Ano Novo", "Carnaval", "Sexta-feira Santa", "Páscoa", "Tiradentes", 
            "Dia do Trabalho", "Corpus Christi", "Independência do Brasil", 
            "Nossa Senhora Aparecida", "Finados", "Proclamação da República", "Natal"
    );

    private static final List<LocalDate> DATES_2024 = Arrays.asList(
            LocalDate.of(2024, 1, 1),  // Ano Novo
            LocalDate.of(2024, 2, 12), // Carnaval
            LocalDate.of(2024, 3, 29), // Sexta-feira Santa
            LocalDate.of(2024, 3, 31), // Páscoa
            LocalDate.of(2024, 4, 21), // Tiradentes
            LocalDate.of(2024, 5, 1),  // Dia do Trabalho
            LocalDate.of(2024, 5, 30), // Corpus Christi
            LocalDate.of(2024, 9, 7),  // Independência do Brasil
            LocalDate.of(2024, 10, 12),// Nossa Senhora Aparecida
            LocalDate.of(2024, 11, 2), // Finados
            LocalDate.of(2024, 11, 15),// Proclamação da República
            LocalDate.of(2024, 12, 25) // Natal
    );

    private static final List<LocalDate> DATES_2025 = Arrays.asList(
            LocalDate.of(2025, 1, 1),  // Ano Novo
            LocalDate.of(2025, 3, 3),  // Carnaval
            LocalDate.of(2025, 4, 18), // Sexta-feira Santa
            LocalDate.of(2025, 4, 20), // Páscoa
            LocalDate.of(2025, 4, 21), // Tiradentes
            LocalDate.of(2025, 5, 1),  // Dia do Trabalho
            LocalDate.of(2025, 6, 19), // Corpus Christi
            LocalDate.of(2025, 9, 7),  // Independência do Brasil
            LocalDate.of(2025, 10, 12),// Nossa Senhora Aparecida
            LocalDate.of(2025, 11, 2), // Finados
            LocalDate.of(2025, 11, 15),// Proclamação da República
            LocalDate.of(2025, 12, 25) // Natal
    );

    /**
     * Método chamado na inicialização da aplicação para criar e persistir eventos de exemplo.
     *
     * @param ev Evento de inicialização da aplicação.
     */
    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        if (isDatabaseEmpty()) {
            createEventsForYear(DATES_2024, 2024);
            createEventsForYear(DATES_2025, 2025);
        }
    }

    private boolean isDatabaseEmpty() {
        long count = em.createQuery("SELECT COUNT(e) FROM CalendarEvent e", Long.class).getSingleResult();
        return count == 0;
    }

    private void createEventsForYear(List<LocalDate> dates, int year) {
        for (int i = 0; i < TITLES.size(); i++) {
            CalendarEvent event = new CalendarEvent();
            event.title = TITLES.get(i);
            event.start = dates.get(i).atStartOfDay().atOffset(ZoneOffset.UTC);
            event.endDate = dates.get(i).atStartOfDay().plusDays(1).atOffset(ZoneOffset.UTC);
            event.allDay = true;
            event.url = "";

            ExtendedProps extendedProps = new ExtendedProps();
            extendedProps.location = "Brasil";
            extendedProps.description = "Feriado Nacional";
            extendedProps.calendar = "Feriados";

            extendedProps.persist();

            event.extendedProps = extendedProps;
            event.persist();
        }
    }
}