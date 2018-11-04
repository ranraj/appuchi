package com.yali.domain.service

import java.util.UUID

import com.yali.domain.model.Task
import com.yali.intf.DBTestSupport
import com.yali.domain.ValidationFailedException
import com.yali.domain.payload.TaskRequest
import com.yali.domain.repository.TaskRepository
import org.mockito.Matchers.{any, same}
import org.mockito.Mockito

class TaskServiceTest extends DBTestSupport {

    trait Fixture {
        val userId = UUID.randomUUID()
        val taskRequest = TaskRequest(title = "title1", details = "details1")
        val task = Task(userId = UUID.randomUUID(),
            title = "title1",
            details = "details1")

        implicit val taskRepo = Mockito.mock(classOf[TaskRepository])
        Mockito.when(taskRepo.create(same(userId), any())(any())).thenReturn(task)
        Mockito.when(taskRepo.update(same(userId), any())(any())).thenReturn(task)
        Mockito.when(taskRepo.find(same(userId), any())(any())).thenReturn(Some(task))
        Mockito.when(taskRepo.delete(same(userId), any())(any())).thenReturn(true)

        val taskService = new TaskService()
    }

    "TaskService" should "create tasks" in new Fixture {
        taskService.create(userId, taskRequest).complete shouldBe false
    }
    it should "validate the request" in new Fixture {
        assertThrows[ValidationFailedException] {
            taskService.create(userId, taskRequest.copy(details = null))
        }
    }
    it should "find tasks" in new Fixture {
        val response = taskService.find(userId, task.id)
        response should not be null
        response.complete shouldEqual false
        response.details shouldEqual "details1"
        response.title shouldEqual "title1"
        response.dueDate shouldEqual None
    }
    it should "validate on update tasks" in new Fixture {
        val response = taskService.update(userId, task.id, taskRequest)
        response should not be null
        response.title shouldEqual "title1"

        assertThrows[ValidationFailedException] {
            taskService.update(userId, task.id, taskRequest.copy(title = null))
        }
    }

    "TaskService validation" should "validate title" in new Fixture {
        taskService.validateForm(taskRequest.copy(title = null)).isInvalid shouldBe true
    }

    it should "validate details" in new Fixture {
        taskService.validateForm(taskRequest.copy(details = null)).isInvalid shouldBe true
    }

}
