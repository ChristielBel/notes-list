package com.example.noteslist.data.repository

import com.example.noteslist.data.db.NotesDao
import com.example.noteslist.data.mapper.toDomain
import com.example.noteslist.data.mapper.toEntity
import com.example.noteslist.domain.model.Note
import com.example.noteslist.domain.repository.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDateTime

class NoteRepositoryImpl(private val dao: NotesDao) : NoteRepository {

    override fun observeNotes(): Flow<List<Note>> {
        return dao.observeNotes().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addNote(note: Note) {
        dao.insert(note.toEntity())
    }

    override suspend fun updateNote(note: Note) {
        dao.insert(note.toEntity())
    }

    override suspend fun initIfEmpty() {
        if (dao.count() == 0) {
            generateInitialNotes().forEach {
                dao.insert(it.toEntity())
            }
        }
    }

    private fun generateInitialNotes(): List<Note> {
        return listOf(
            Note(
                id = 1,
                title = "Купить продукты",
                text = "Молоко, хлеб, сыр, фрукты и что-нибудь к чаю",
                createdAt = LocalDateTime.now().minusHours(1),
                isImportant = false,
                isRead = false
            ),
            Note(
                id = 2,
                title = "Подготовить презентацию",
                text = "Сделать 10 слайдов по архитектуре Android приложения",
                createdAt = LocalDateTime.now().minusDays(1),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 3,
                title = "Позвонить врачу",
                text = "Записаться на приём на следующую неделю",
                createdAt = LocalDateTime.now().minusDays(2),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 4,
                title = "Забрать посылку",
                text = "Посылка с Wildberries, код получения в приложении",
                createdAt = LocalDateTime.now().minusHours(5),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 5,
                title = "Идеи для проекта",
                text = "Добавить тёмную тему, сортировку по дате и поиск по заметкам",
                createdAt = LocalDateTime.now().minusDays(3),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 6,
                title = "Заплатить за интернет",
                text = "Оплатить до 25 числа, иначе отключат",
                createdAt = LocalDateTime.now().minusDays(4),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 7,
                title = "Купить подарок маме",
                text = "День рождения через 2 недели, нужно подумать над подарком",
                createdAt = LocalDateTime.now().minusDays(5),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 8,
                title = "Записаться в спортзал",
                text = "Посмотреть абонементы рядом с домом и выбрать удобное время",
                createdAt = LocalDateTime.now().minusHours(12),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 9,
                title = "Прочитать статью по Coroutines",
                text = "Отложить в закладки статью про корутины на Medium",
                createdAt = LocalDateTime.now().minusDays(6),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 10,
                title = "Сдать отчёт",
                text = "Подготовить отчёт за прошлый месяц до пятницы",
                createdAt = LocalDateTime.now().minusDays(7),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 11,
                title = "Встреча с друзьями",
                text = "Забронировать столик в ресторане на субботу, 19:00",
                createdAt = LocalDateTime.now().minusDays(8),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 12,
                title = "Купить билеты в кино",
                text = "На новый фильм в выходные, проверить расписание",
                createdAt = LocalDateTime.now().minusHours(3),
                isImportant = false,
                isRead = false
            ),
            Note(
                id = 13,
                title = "Выучить новые слова по английскому",
                text = "Повторить 20 новых слов и выполнить упражнения",
                createdAt = LocalDateTime.now().minusDays(9),
                isImportant = false,
                isRead = true
            ),
            Note(
                id = 14,
                title = "Обновить резюме",
                text = "Добавить новые проекты на GitHub и обновить LinkedIn",
                createdAt = LocalDateTime.now().minusDays(10),
                isImportant = true,
                isRead = false
            ),
            Note(
                id = 15,
                title = "Сходить в химчистку",
                text = "Забрать пальто и сдать куртку",
                createdAt = LocalDateTime.now().minusDays(11),
                isImportant = false,
                isRead = true
            )
        )
    }
}