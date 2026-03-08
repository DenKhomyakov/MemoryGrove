package com.memorygrove.backend.service;

import com.memorygrove.backend.dictionary.ExerciseType;
import com.memorygrove.backend.entity.dbo.ExerciseStats;
import com.memorygrove.backend.entity.dbo.User;
import com.memorygrove.backend.entity.dto.brain.request.ArithmeticChainRequestDto;
import com.memorygrove.backend.entity.dto.brain.request.TextComprehensionRequestDto;
import com.memorygrove.backend.entity.dto.brain.response.ArithmeticChainResponseDto;
import com.memorygrove.backend.entity.dto.brain.response.ExerciseStatsResponseDto;
import com.memorygrove.backend.repository.ExerciseStatsRepository;
import com.memorygrove.backend.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Сервис для работы с упражнениями для тренировки памяти
 * <p>
 * Предоставляет функционал для:
 * <ul>
 *   <li>Генерации арифметических цепочек</li>
 *   <li>Проверки ответов на арифметические цепочки</li>
 *   <li>Обработки результатов понимания текста</li>
 *   <li>Ведения статистики по упражнениям</li>
 * </ul>
 * </p>
 */
@Log4j2
@Service
@RequiredArgsConstructor
@Transactional
public class BrainTrainerService {

    private final ExerciseStatsRepository statsRepository;
    private final UserRepository userRepository;
    private final SecureRandom random = new SecureRandom();

    /**
     * Генерирует новую арифметическую цепочку для решения
     *
     * @param length длина цепочки (количество операций)
     * @return DTO с выражением и правильным ответом
     */
    public ArithmeticChainResponseDto generateArithmeticChain(int length) {
        log.info("Генерация арифметической цепочки длиной {}", length);

        // Ограничиваем длину разумными пределами
        length = Math.min(Math.max(length, 1), 10);

        StringBuilder expression = new StringBuilder();
        int result = random.nextInt(10) + 1; // Начинаем с числа от 1 до 10
        expression.append(result);

        for (int i = 0; i < length; i++) {
            boolean isAddition = random.nextBoolean();
            int number = random.nextInt(5) + 1; // Число от 1 до 5

            if (isAddition) {
                expression.append(" + ").append(number);
                result += number;
            } else {
                expression.append(" - ").append(number);
                result -= number;
            }
        }

        log.debug("Сгенерировано выражение: {} = {}", expression.toString(), result);

        return new ArithmeticChainResponseDto(
                expression.toString(),
                result,
                length
        );
    }

    /**
     * Проверяет ответ пользователя на арифметическую цепочку и обновляет статистику
     *
     * @param userId ID пользователя
     * @param correctResult правильный результат
     * @param requestDto DTO с ответом пользователя
     * @return true если ответ верный
     */
    public boolean checkArithmeticAnswer(UUID userId, int correctResult, ArithmeticChainRequestDto requestDto) {
        log.info("Проверка ответа на арифметическую цепочку для пользователя {}", userId);

        boolean isCorrect = requestDto.getResult() == correctResult;

        // Обновляем статистику
        updateStats(userId, ExerciseType.ARITHMETIC, isCorrect,
                requestDto.getChainLength(), requestDto.getTimeSeconds());

        log.info("Результат: {}. Ответ пользователя: {}, правильный: {}",
                isCorrect ? "✅ верно" : "❌ неверно",
                requestDto.getResult(), correctResult);

        return isCorrect;
    }

    /**
     * Обрабатывает результат упражнения "Понимание текста" и обновляет статистику
     *
     * @param userId ID пользователя
     * @param requestDto DTO с результатами понимания текста
     */
    public void processTextComprehensionResult(UUID userId, TextComprehensionRequestDto requestDto) {
        log.info("Обработка результатов понимания текста для пользователя {}", userId);

        // Считаем упражнение успешным, если пользователь ответил правильно на 70% и более вопросов
        boolean isSuccess = requestDto.getSuccessRate() >= 70;

        // Обновляем статистику
        updateStats(userId, ExerciseType.TEXT_COMPREHENSION, isSuccess,
                requestDto.getCorrectAnswers(), requestDto.getTimeSeconds());

        log.info("✅ Результаты обработаны. Успешно: {}, правильных ответов: {} из {}, время: {} сек",
                isSuccess, requestDto.getCorrectAnswers(), requestDto.getTotalQuestions(), requestDto.getTimeSeconds());
    }

    /**
     * Получает статистику пользователя по всем упражнениям
     *
     * @param userId ID пользователя
     * @return список статистики по каждому упражнению
     */
    @Transactional(readOnly = true)
    public List<ExerciseStatsResponseDto> getAllStats(UUID userId) {
        log.info("Получение всей статистики упражнений для пользователя {}", userId);

        return statsRepository.findByUserIdOrderByLastAttemptAtDesc(userId)
                .stream()
                .map(ExerciseStatsResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    /**
     * Получает статистику пользователя по конкретному типу упражнения
     *
     * @param userId ID пользователя
     * @param exerciseType тип упражнения
     * @return статистика по упражнению
     */
    @Transactional(readOnly = true)
    public ExerciseStatsResponseDto getStatsByType(UUID userId, ExerciseType exerciseType) {
        log.info("Получение статистики по упражнению {} для пользователя {}", exerciseType, userId);

        return statsRepository.findByUserIdAndExerciseType(userId, exerciseType)
                .map(ExerciseStatsResponseDto::fromEntity)
                .orElseGet(() -> createEmptyStatsDto(userId, exerciseType));
    }

    /**
     * Обновляет статистику после выполнения упражнения
     *
     * @param userId ID пользователя
     * @param exerciseType тип упражнения
     * @param isSuccess была ли попытка успешной
     * @param score полученный результат (опционально)
     * @param timeSeconds затраченное время (опционально)
     */
    private void updateStats(UUID userId, ExerciseType exerciseType,
                             boolean isSuccess, Integer score, Integer timeSeconds) {
        log.debug("Обновление статистики для пользователя {} по упражнению {}", userId, exerciseType);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("Пользователь с ID '%s' не найден", userId)
                ));

        ExerciseStats stats = statsRepository
                .findByUserIdAndExerciseType(userId, exerciseType)
                .orElseGet(() -> {
                    ExerciseStats newStats = new ExerciseStats();
                    newStats.setUser(user);
                    newStats.setExerciseType(exerciseType);
                    return newStats;
                });

        stats.updateStats(isSuccess, score, timeSeconds);
        statsRepository.save(stats);

        log.debug("✅ Статистика обновлена: попыток={}, успехов={}, успешность={}%",
                stats.getAttempts(), stats.getSuccesses(), stats.getSuccessRate());
    }

    /**
     * Создаёт пустой DTO со статистикой, если запись ещё не создана
     *
     * @param userId ID пользователя
     * @param exerciseType тип упражнения
     * @return DTO с нулевой статистикой
     */
    private ExerciseStatsResponseDto createEmptyStatsDto(UUID userId, ExerciseType exerciseType) {
        ExerciseStatsResponseDto dto = new ExerciseStatsResponseDto();
        dto.setExerciseType(exerciseType);
        dto.setDisplayName(getDisplayName(exerciseType));
        dto.setAttempts(0);
        dto.setSuccesses(0);
        dto.setSuccessRate(0);
        dto.setBestScore(null);
        dto.setBestTimeSeconds(null);
        dto.setLastAttemptAt(null);
        return dto;
    }

    /**
     * Получает отображаемое название упражнения по его типу
     *
     * @param type тип упражнения
     * @return отображаемое название
     */
    private String getDisplayName(ExerciseType type) {
        switch (type) {
            case ARITHMETIC:
                return "🧮 Арифметическая цепочка";
            case TEXT_COMPREHENSION:
                return "📖 Понимание текста";
            case POEM:
                return "📝 Заучивание стихов";
            case ASSOCIATION:
                return "🔗 Ассоциации";
            case SEQUENCE:
                return "📊 Продолжи ряд";
            case REVERSE_WORD:
                return "🔄 Слово наоборот";
            case ODD_ONE_OUT:
                return "❓ Что лишнее?";
            case SCHULTE_TABLE:
                return "🔢 Таблица Шульте";
            default:
                return type.name();
        }
    }
}
