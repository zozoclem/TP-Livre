package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort

class BookNotFoundException(id: Int) : RuntimeException("Book with id $id not found")

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book): Book {
        return bookPort.createBook(book)
    }

    fun reserveBook(id: Int): Book {
        val book = bookPort.getBookById(id) ?: throw BookNotFoundException(id)
        return bookPort.reserveBook(book.reserve())
    }
}
