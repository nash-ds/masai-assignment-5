## How to run

```
./mvnw spring-boot:run
```

 The application uses the `dev` profile by default, with H2 and Flyway enabled.

 Alternatively:

```
./mvnw clean package
java -jar target/<artifact-name>.jar
```

 Swagger UI:

```
http://localhost:8080/swagger-ui/index.html
```

 ## Endpoint table

 | Method | Path | Expected status | Purpose |
| --- | --- | --- | --- |
| GET | `/api/policies` | 200 | List all policies |
| GET | `/api/policies/{policyNo}` | 200 / 404 | Get policy details |
| GET | `/api/policies?status=Active` | 200 | Filter by status |
| GET | `/api/policies?type=TERM` | 200 | Filter by product type |
| GET | `/api/policies?customer=Anita%20Sharma` | 200 | Filter by customer |
| GET | `/api/policies/search?minPremium=20000` | 200 / 400 | Premium search |
| POST | `/api/policies` | 201 / 400 / 409 | Create policy |
| DELETE | `/api/policies/{policyNo}` | 204 / 404 | Delete policy |
| GET | `/api/policies/{policyNo}/claims` | 200 / 404 | List policy claims |
| POST | `/api/claims` | 201 / 400 / 404 | Create claim |

## Which side owns `policy_riders`

 `Policy` is the **owning side** of the `Policy ↔ Rider` many-to-many relationship. It declares `@ManyToMany` together with `@JoinTable(name = "policy_riders", ...)`, while `Rider` uses `mappedBy`. The `policy_riders` table therefore contains `policy_id` and `rider_id` as its composite primary key. No `CascadeType.ALL` should be used on this many-to-many relationship.

 ## Why return DTOs when `open-in-view` is false?

 With `spring.jpa.open-in-view=false`, the Hibernate persistence context is closed before the controller response is serialized.\
 If a controller returns a `Policy` entity containing a lazy `riders` collection, Jackson may access `riders` after the session has closed.\
 That causes a `LazyInitializationException` because Hibernate can no longer load the collection.\
 Returning DTOs allows the service layer to fetch the required data inside a transaction and map it to a stable API response.\
 It also prevents exposing JPA entities and their persistence relationships directly through the REST API.
