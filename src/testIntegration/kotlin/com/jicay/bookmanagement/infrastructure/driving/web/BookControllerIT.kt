package com.jicay.bookmanagement.infrastructure.driving.web

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.usecase.BookNotFoundException
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
class BookControllerIT(
    @MockkBean private val bookUseCase: BookUseCase,
    private val mockMvc: MockMvc
) : FunSpec({
    extension(SpringExtension)

    test("rest route get books") {
        every { bookUseCase.getAllBooks() } returns listOf(Book(id = 1, name = "A", author = "B", reserved = false))

        mockMvc.get("/books")
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content {
                    json(
                        """
                        [
                          {
                            "id": 1,
                            "name": "A",
                            "author": "B",
                            "reserved": false
                          }
                        ]
                        """.trimIndent()
                    )
                }
            }
    }

    test("rest route post book") {
        every { bookUseCase.addBook(any()) } returns Book(id = 1, name = "Les misérables", author = "Victor Hugo", reserved = false)

        mockMvc.post("/books") {
            content =
                """
                {
                  "name": "Les misérables",
                  "author": "Victor Hugo"
                }
                """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
            content {
                json(
                    """
                    {
                      "id": 1,
                      "name": "Les misérables",
                      "author": "Victor Hugo",
                      "reserved": false
                    }
                    """.trimIndent()
                )
            }
        }

        verify(exactly = 1) { bookUseCase.addBook(Book(name = "Les misérables", author = "Victor Hugo")) }
    }

    test("rest route post book should return 400 when body is not good") {
        mockMvc.post("/books") {
            content =
                """
                {
                  "title": "Les misérables",
                  "author": "Victor Hugo"
                }
                """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { bookUseCase.addBook(any()) }
    }

    test("rest route reserve book") {
        every {
            bookUseCase.reserveBook(1)
        } returns Book(id = 1, name = "Les misérables", author = "Victor Hugo", reserved = true)

        mockMvc.post("/books/1/reserve") {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
            content {
                json(
                    """
                    {
                      "id": 1,
                      "name": "Les misérables",
                      "author": "Victor Hugo",
                      "reserved": true
                    }
                    """.trimIndent()
                )
            }
        }

        verify(exactly = 1) { bookUseCase.reserveBook(1) }
    }

    test("rest route reserve missing book should return 404") {
        every { bookUseCase.reserveBook(10) } throws BookNotFoundException(10)

        mockMvc.post("/books/10/reserve") {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }
    }
})
