package com.compiler.server

import com.compiler.server.generator.TestProjectRunner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class ConcurrencyRunnerTest {
  @Autowired
  private lateinit var testRunner: TestProjectRunner

  @Test
  fun `a lot of hello word test JVM`() {
    runManyTest {
      testRunner.run(
        code = "fun main() {\n println(\"Hello, world!!!\")\n}",
        contains = "Hello, world!!!"
      )
    }
  }

  @Test
  fun `a lot of hello word test JS`() {
    runManyTest {
      testRunner.runJs(
        code = "fun main() {\n println(\"Hello, world!!!\")\n}",
        contains = "println('Hello, world!!!');"
      )
    }
  }

  private fun runManyTest(times: Int = 100, test: () -> Unit) {
    runBlocking {
      GlobalScope.launch(Dispatchers.IO) {
        for (i in 0 until times) {
          launch {
            test()
          }
        }
      }.join()
    }
  }

}