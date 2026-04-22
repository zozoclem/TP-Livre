package com.jicay.bookmanagement.infrastructure.driven.adapter

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate) : BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM book", MapSqlParameterSource()) { rs, _ ->
                Book(
                    id = rs.getInt("id"),
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    reserved = rs.getBoolean("reserved")
                )
            }
    }

    override fun createBook(book: Book): Book {
        return namedParameterJdbcTemplate
            .queryForObject(
                """
                INSERT INTO book (title, author, reserved)
                VALUES (:title, :author, :reserved)
                RETURNING id, title, author, reserved
                """.trimIndent(),
                mapOf(
                    "title" to book.name,
                    "author" to book.author,
                    "reserved" to book.reserved
                )
            ) { rs, _ ->
                Book(
                    id = rs.getInt("id"),
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    reserved = rs.getBoolean("reserved")
                )
            }!!
    }

    override fun getBookById(id: Int): Book? {
        return namedParameterJdbcTemplate
            .query(
                "SELECT * FROM book WHERE id = :id",
                mapOf("id" to id)
            ) { rs, _ ->
                Book(
                    id = rs.getInt("id"),
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    reserved = rs.getBoolean("reserved")
                )
            }
            .firstOrNull()
    }

    override fun reserveBook(book: Book): Book {
        namedParameterJdbcTemplate.update(
            "UPDATE book SET reserved = :reserved WHERE id = :id",
            mapOf(
                "id" to book.id,
                "reserved" to book.reserved
            )
        )
        return book
    }
}
