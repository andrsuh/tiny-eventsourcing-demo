# Tiny event sourcing library demo
This project demonstrates how easily you can build your event-driven, event sourcing based application POC in 15 minutes
using [Tiny Event Sourcing library](https://github.com/andrsuh/tiny-event-sourcing)

### Run MongoDb
This example uses MongoDb as an implementation of the Event store. You can see it in `pom.xml`:

```
<dependency>
    <groupId>ru.quipy</groupId>
    <artifactId>tiny-mongo-event-store-spring-boot-starter</artifactId>
    <version>${tiny.es.version}</version>
</dependency>
```

Thus, you have to run MongoDb in order to test this example. We have `docker-compose` file in the root. Run following command to start the database:

```
docker-compose up
```

### Run the application
To make the application run you can start the main class `DemoApplication`.

### Test the endpoints
There are a couple of REST endpoints you can try to call.

To create new Project with name "Project" and creator user "Andrey" call:

```
POST http://localhost:8080/projects/Project?creatorId="Andrey"
```

As a response you will receive the corresponding event if everything went well:

```
{
    "projectId": "823d4576-5e95-4027-bd9e-63b27086256c",
    "title": "Project",
    "creatorId": "\"Andrey\"",
    "createdAt": 1672078429244,
    "id": "e18fb19b-96eb-4706-b030-6cc5f7c23de2",
    "name": "PROJECT_CREATED_EVENT",
    "version": 1
}
```


Now lets add some Task with name "Task" to the project. Take the projectId from previous response and perform:

```
POST http://localhost:8081/projects/823d4576-5e95-4027-bd9e-63b27086256c/tasks/Task 
```

You will receive corresponding `TASK_CREATED_EVENT` if everything ok

Now lets fetch the current state of the ProjectAggregate:

```
GET http://localhost:8081/projects/823d4576-5e95-4027-bd9e-63b27086256c
```

You will receive something like this:

```
{
    "createdAt": 1672078820917,
    "updatedAt": 1672078820917,
    "projectTitle": "Project",
    "creatorId": "Andrey",
    "tasks": {
        "4b2e75be-19c8-4504-82b1-be3a8775a21a": {
            "id": "4b2e75be-19c8-4504-82b1-be3a8775a21a",
            "name": "Task",
            "tagsAssigned": []
        }
    },
    "projectTags": {},
    "id": "823d4576-5e95-4027-bd9e-63b27086256c"
}
```
 This is the project with the only task inside.