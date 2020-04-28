package com.gezanoletti.demopartialcache;

import java.util.List;

public interface PersonService
{
    List<PersonDto> findPersonsOneAtTime(final List<Long> ids);

    List<PersonDto> findPersonsAllAtOnce(final List<Long> ids);
}
