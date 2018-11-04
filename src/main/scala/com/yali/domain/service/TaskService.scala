package com.yali.domain.service

import cats.data.Validated.{Invalid, Valid}
import cats.implicits._
import Validator.{ValidationResult, notNull, success}
import com.yali.domain.model.{ID, Task}
import com.yali.domain.payload.{TaskRequest, TaskResponse}
import com.yali.domain.{NotFoundException, ValidationFailedException}
import com.yali.domain.repository.TaskRepository
import scalikejdbc.DB


class TaskService(implicit taskRepository: TaskRepository) {


    def findAll(userId: ID): List[TaskResponse] =
        DB readOnly { implicit session => taskRepository.findAll(userId).map(toTaskResponse) }

    def find(userId: ID, taskId: ID): TaskResponse = DB readOnly { implicit session =>
        toTaskResponse(taskRepository.find(userId, taskId).getOrElse(throw new NotFoundException("task not found")))
    }

    def create(userId: ID, req: TaskRequest): TaskResponse =
        validateForm(req) match {
            case Valid(_) => toTaskResponse(DB localTx { implicit session =>
                taskRepository.create(userId, toTask(userId, req))
            })
            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }

    def update(userId: ID, taskId: ID, req: TaskRequest): TaskResponse =
        validateForm(req) match {
            case Valid(_) =>
                DB localTx { implicit session =>
                    taskRepository.find(userId, taskId) match {
                        case Some(existingTask) =>
                            toTaskResponse(taskRepository.update(userId, updateFields(existingTask, req)))
                        case _ => throw new NotFoundException("not found")
                    }
                }
            case Invalid(errors) => throw new ValidationFailedException(errors.toList)
        }

    def delete(userId: ID, taskId: ID): Unit = {
        DB localTx { implicit session =>
            if (!taskRepository.delete(userId, taskId)) {
                throw new NotFoundException("task not found")
            }
        }
    }

    def validateForm(req: TaskRequest): ValidationResult[TaskRequest] = (
            notNull("title", req.title),
            notNull("email", req.details),
            success(req.dueDate),
            success(req.complete)
    ).mapN(TaskRequest)

    def updateFields(task: Task, req: TaskRequest): Task = {
        task.copy(title = req.title,
            details = req.details,
            dueDate = req.dueDate,
            complete = req.complete.getOrElse(task.complete)
        )
    }

    def toTaskResponse(task: Task) =
        TaskResponse(
            id = task.id,
            title = task.title,
            details = task.details,
            dueDate = task.dueDate,
            complete = task.complete)

    def toTask(userId: ID, req: TaskRequest): Task =
        Task(
            userId = userId,
            title = req.title,
            details = req.details,
            dueDate = req.dueDate)
}