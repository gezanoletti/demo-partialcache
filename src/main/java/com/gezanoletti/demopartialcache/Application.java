package com.gezanoletti.demopartialcache;

import java.util.List;
import java.util.Map;

import static java.lang.System.out;

public class Application
{
    private static Map cacheRepository = Map.of(
        1L, new PersonDto(1L, "Name1", "Surname1"),
        4L, new PersonDto(4L, "Name4", "Surname4")
    );

    private static Map dbRepository = Map.of(
        1L, new PersonDto(1L, "Name1", "Surname1"),
        2L, new PersonDto(2L, "Name2", "Surname2"),
        3L, new PersonDto(3L, "Name3", "Surname3"),
        4L, new PersonDto(4L, "Name4", "Surname4"),
        5L, new PersonDto(5L, "Name5", "Surname5")
    );

    private static PersonService personService = new DefaultPersonService(cacheRepository, dbRepository);


    public static void main(String[] args)
    {
        var ids = List.of(1L, 2L, 3L, 4L, 5L);
        var persons = personService.findPersonsAllAtOnce(ids);
        persons.forEach(out::println);
    }
}

