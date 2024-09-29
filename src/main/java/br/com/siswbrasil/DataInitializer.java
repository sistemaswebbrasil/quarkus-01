package br.com.siswbrasil;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import br.com.siswbrasil.model.CalendarEvent;
import br.com.siswbrasil.model.ExtendedProps;
import br.com.siswbrasil.model.Guest;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;

/**
 * Classe responsável por inicializar dados de exemplo na inicialização da aplicação.
 */
@ApplicationScoped
public class DataInitializer {

    private static final List<String> TITLES = Arrays.asList(
            "Finalizar inoperancia", "Reunião de planejamento", "Revisão do projeto", 
            "Apresentação de resultados", "Treinamento de equipe", "Discussão de orçamento",
            "Reunião com clientes", "Sessão de brainstorming", "Revisão de código", 
            "Planejamento estratégico"
    );
    private static final List<String> LOCATIONS = Arrays.asList(
            "Pame", "Sala de Reuniões", "Auditório", "Escritório Central", "Sala de Treinamento",
            "Sala de Conferências", "Sala de Projetos", "Sala de TI", "Sala de Marketing", 
            "Sala de Vendas"
    );
    private static final List<String> DESCRIPTIONS = Arrays.asList(
            "Terminar o projeto", "Planejar as próximas etapas", "Revisar o progresso", 
            "Apresentar os resultados finais", "Treinar a nova equipe", "Discutir o orçamento",
            "Reunião com clientes importantes", "Sessão de brainstorming para novas ideias", 
            "Revisar o código do projeto", "Planejar a estratégia para o próximo trimestre"
    );
    private static final List<String> GUEST_NAMES = Arrays.asList(
            "Jane Foster", "Sandy Vega", "John Doe", "Alice Smith", "Bob Johnson",
            "Michael Brown", "Emily Davis", "Chris Wilson", "Jessica Garcia", "David Martinez"
    );

    private final Random random = new Random();

    /**
     * Método chamado na inicialização da aplicação para criar e persistir eventos de exemplo.
     *
     * @param ev Evento de inicialização da aplicação.
     */
    @Transactional
    public void onStart(@Observes StartupEvent ev) {
        for (int i = 1; i <= 29; i++) {
            CalendarEvent event = new CalendarEvent();
            event.title = getRandomElement(TITLES);
            event.start = getRandomDateTime();
            event.endDate = getRandomDateTime().plusDays(1);
            event.allDay = random.nextBoolean();
            event.url = "";

            ExtendedProps extendedProps = new ExtendedProps();
            extendedProps.location = getRandomElement(LOCATIONS);
            extendedProps.description = getRandomElement(DESCRIPTIONS);
            extendedProps.calendar = "Business";

            // Criar e persistir convidados
            Set<Guest> guests = new HashSet<>();
            while (guests.size() < 2) {
                Guest guest = new Guest();
                guest.name = getRandomElement(GUEST_NAMES);
                guests.add(guest);
                guest.persist();
            }

            extendedProps.guestList = Arrays.asList(guests.toArray(new Guest[0]));
            extendedProps.persist();

            event.extendedProps = extendedProps;
            event.persist();
        }
    }

    /**
     * Retorna um elemento aleatório de uma lista.
     *
     * @param list Lista de onde o elemento será selecionado.
     * @return Elemento aleatório da lista.
     */
    private String getRandomElement(List<String> list) {
        return list.get(random.nextInt(list.size()));
    }

    /**
     * Gera uma data e hora aleatória dentro de um período de um ano a partir da data atual.
     *
     * @return Data e hora aleatória.
     */
    private OffsetDateTime getRandomDateTime() {
        LocalDateTime now = LocalDateTime.now();
        int daysToAdd = random.nextInt(365);
        int hour = random.nextInt(24);
        int minute = random.nextInt(60);
        return now.plusDays(daysToAdd).withHour(hour).withMinute(minute).withSecond(0).withNano(0).atOffset(OffsetDateTime.now().getOffset());
    }
}