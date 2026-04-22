package com.jicay.bookmanagement.domain.model

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class BookTest : FunSpec({

    test("name should not be empty") {
        shouldThrow<IllegalArgumentException> { Book(name = "", author = "Victor Hugo") }
    }

    test("author should not be empty") {
        shouldThrow<IllegalArgumentException> { Book(name = "Les Misérables", author = "") }
    }

    test("reserve a book") {
        val reservedBook = Book(id = 1, name = "Les Misérables", author = "Victor Hugo").reserve()

        reservedBook.reserved shouldBe true
    }

    test("already reserved book can not be reserved again") {
        shouldThrow<IllegalArgumentException> {
            Book(id = 1, name = "Les Misérables", author = "Victor Hugo", reserved = true).reserve()
        }
    }
})
