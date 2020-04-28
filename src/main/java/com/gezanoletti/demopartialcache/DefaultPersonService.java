package com.gezanoletti.demopartialcache;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Stream;
import io.vavr.control.Either;
import io.vavr.control.Option;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
class DefaultPersonService implements PersonService
{
    private final Map<Long, PersonDto> cacheRepository;
    private final Map<Long, PersonDto> dbRepository;


    public List<PersonDto> findPersonsOneAtTime(final List<Long> ids)
    {
        return Stream.ofAll(ids)
            .map(this::loadFromCache)
            .map(this::loadFromDbOneAtTime)
            .toStream()
            .map(Either::get)
            .collect(toList());
    }


    private Either<Long, PersonDto> loadFromDbOneAtTime(final Either<Long, PersonDto> either)
    {
        if (either.isRight())
        {
            return either;
        }

        var personDto = dbRepository.get(either.getLeft());
        return Option
            .of(personDto)
            .toEither(either.getLeft());
    }


    public List<PersonDto> findPersonsAllAtOnce(final List<Long> ids)
    {
        return Stream.ofAll(ids)
            .map(this::loadFromCache)
            .groupBy(Either::isRight)
            .map(this::loadFromDbAllAtOnce)
            .values()
            .flatMap(Function.identity())
            .toStream()
            .map(Either::get)
            .collect(toList());
    }


    private Tuple2<Boolean, Stream<Either<Long, PersonDto>>> loadFromDbAllAtOnce(
        final boolean isCached,
        final Stream<Either<Long, PersonDto>> eithers)
    {
        return isCached
            ? Tuple.of(true, eithers)
            : Tuple.of(false, fetchFromDb(eithers.map(Either::getLeft).toJavaList()));
    }


    private Stream<Either<Long, PersonDto>> fetchFromDb(final List<Long> ids)
    {
        return Stream.ofAll(ids)
            .map(id -> Option.of(dbRepository.get(id)).toEither(id));
    }


    private Either<Long, PersonDto> loadFromCache(final Long id)
    {
        var personDto = (PersonDto) cacheRepository.get(id);
        return Option
            .of(personDto)
            .toEither(id);
    }
}
