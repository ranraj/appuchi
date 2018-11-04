package com.yali.domain.repository

import java.time.OffsetDateTime
import java.util.UUID

import com.yali.domain.model.{ID, Task}
import scalikejdbc._

class TaskRepository extends RepositoryHelper {
    val t = Task.syntax("t")

    def findAll(userId: ID)(implicit session: DBSession): List[Task] =
        sql"""select ${t.result.*} from ${Task.as(t)} where ${t.userId} = $userId """
                .map(Task(t.resultName)).list.apply()

    def find(userId: ID, taskId: ID)(implicit session: DBSession): Option[Task] =
        sql"""select ${t.result.*} from ${Task.as(t)} where ${t.id} = $taskId and ${t.userId} = $userId """
                .map(Task(t.resultName)).single.apply()

    def create(userId: ID, task: Task)(implicit session: DBSession): Task = {
        sql"""insert into task (id, user_id, title, details, due_date, complete, created_at, modified_at) values (
                 ${task.id},
                 $userId,
                    ${task.title},
                    ${task.details},
                    ${task.dueDate},
                    ${task.complete},
                    ${task.createdAt},
                    ${task.modifiedAt})""".update.apply()
        mustExist(find(userId, task.id))
    }

    def update(userId: ID, task: Task)(implicit session: DBSession): Task = {
        sql"""update task set
              title = ${task.title},
              details=${task.details},
              due_date=${task.dueDate},
              complete=${task.complete},
              modified_at=${OffsetDateTime.now}
              where id = ${task.id} and user_id = $userId""".update.apply()
        mustExist(find(userId, task.id))
    }

    def delete(userId: ID, taskId: ID)(implicit session: DBSession): Boolean =
        sql"delete from task where id = $taskId and user_id = $userId".update().apply() > 0
}


