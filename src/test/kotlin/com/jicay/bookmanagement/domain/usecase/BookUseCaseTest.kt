package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.clearMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    beforeTest {
        clearMocks(bookPort)
    }

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book(name = "Les Misérables", author = "Victor Hugo"),
            Book(name = "Hamlet", author = "William Shakespeare")
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book(name = "Hamlet", author = "William Shakespeare"),
            Book(name = "Les Misérables", author = "Victor Hugo")
        )
    }

    test("add book") {
        val book = Book(name = "Les Misérables", author = "Victor Hugo")
        every { bookPort.createBook(any()) } returns book.copy(id = 1)

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("reserve book") {
        every { bookPort.getBookById(1) } returns Book(id = 1, name = "Les Misérables", author = "Victor Hugo")
        every { bookPort.reserveBook(any()) } answers { firstArg() }

        val res = bookUseCase.reserveBook(1)

        res.reserved shouldBe true
        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 1) {
            bookPort.reserveBook(Book(id = 1, name = "Les Misérables", author = "Victor Hugo", reserved = true))
        }
    }

    test("reserve unknown book") {
        every { bookPort.getBookById(1) } returns null

        shouldThrow<BookNotFoundException> {
            bookUseCase.reserveBook(1)
        }

        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 0) { bookPort.reserveBook(any()) }
    }

    test("reserve already reserved book") {
        every { bookPort.getBookById(1) } returns Book(id = 1, name = "Les Misérables", author = "Victor Hugo", reserved = true)

        shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook(1)
        }

        verify(exactly = 1) { bookPort.getBookById(1) }
        verify(exactly = 0) { bookPort.reserveBook(any()) }
    }
})