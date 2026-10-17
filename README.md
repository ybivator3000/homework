***
```
homework/
├── docs/
│ ├── requirements.md #Тут лежит ТЗ
│ ├── architecture.md
│ └── adr/
│   ├── ... #Описание паттернов поч зач ипох 
├── .mvn/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── ru/
│   │   │       └── aston/
│   │   │           └── homework/
│   │   │               ├── Main.java
│   │   │               ├── config/
│   │   │               │   ├── HibernateUtil.java
│   │   │               ├──entity/
│   │   │               │   └── User.java
│   │   │               ├── dao/
│   │   │               │   ├── GenericDao.java
│   │   │               │   ├── UserDao.java
│   │   │               │   └── impl/
│   │   │               │       └── UserDaoImpl.java
│   │   │               ├── service/
│   │   │               │   ├── UserService.java
│   │   │               │   └── impl/
│   │   │               │       └── UserServiceImpl.java
│   │   │               ├── dto/
│   │   │               │   ├── UserCreateRequest.java
│   │   │               │   └── UserUpdateRequest.java
│   │   │               ├── ui/
│   │   │               │   ├── ConsoleRunner.java
│   │   │               │   ├── ConsoleMenu.java
│   │   │               │   ├── Command.java
│   │   │               │   └── InputReader.java
│   │   │               ├── exception/
│   │   │               │   ├── DaoException.java
│   │   │               │   └── ServiceException.java
│   │   │               └── util/
│   │   │                   └── UserValidator.java
│   │   └── resources/
│   │       └── ...
│   └── test/
│       └── java/ 
│           └── ru/
│               └── aston/
│                   └── homework/
│                       └── ...
├── .gitignore
├── README.md
└── pom.xml
```