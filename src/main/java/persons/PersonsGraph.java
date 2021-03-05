package persons;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class PersonsGraph {

    private static final Logger LOG = LoggerFactory.getLogger(PersonsGraph.class );

    private Map<Address, Set<Person>> personsByAddress = new HashMap<>();
    private Map<Name, Set<Person>> personsByName = new HashMap<>();
    private Map<Person, Set<Person>> personsGraph= new HashMap<>();

    public static void main(String[] args) {

        Address addressOne = new Address("Bletchey Park", "Cambridge");
        Address addressTwo = new Address("", "London");
        Address addressThree = new Address("", "New York");

        Name fullNameOne = new Name("Alan", "Turing");
        Name fullNameTwo = new Name("Joan", "Clarke");
        Name fullNameThree = new Name("Grace", "Hopper");

        Person person1 = new Person(fullNameOne, addressOne);
        Person person11 = new Person(fullNameOne, addressTwo);
        Person person2 = new Person(fullNameTwo, addressOne);
        Person person21 = new Person(fullNameTwo, addressTwo);
        Person person3 = new Person(fullNameThree, addressThree);

        List<Person> people = new ArrayList<>();
        people.add(person1);
        people.add(person11);
        people.add(person2);
        people.add(person21);
        people.add(person3);


        PersonsGraph personsGraph = new PersonsGraph();

        Person[] personsArray = new Person[5];

        personsGraph.init(people.toArray(personsArray));
        LOG.info(personsGraph.toString());

// {Alan, Turing}, {Bletchey Park, Cambridge}} <-> {Alan, Turing}, {Bletchey Park, Cambridge}} = 0
        printRelationshipLevel(personsGraph, person1, person1);
// {Alan, Turing}, {Bletchey Park, Cambridge} <-> {Joan, Clarke}, {Bletchey Park, Cambridge} = 1
        printRelationshipLevel(personsGraph, person1, person2);
// {Alan, Turing}, {Bletchey Park, Cambridge}} <-> {Alan, Turing}, {London}} = 1
        printRelationshipLevel(personsGraph, person1, person11);
// {Alan, Turing}, {London} <-> {Joan, Clarke}, {London} = 1
        printRelationshipLevel(personsGraph, person11, person21);
// {Alan, Turing}, {Bletchey Park, Cambridge} <-> {Joan, Clarke}, {London}} = 2
        printRelationshipLevel(personsGraph, person1, person21);
// {Alan, Turing}, {Bletchey Park, Cambridge} <-> {Grace, Hopper}, {New York} = -1
        printRelationshipLevel(personsGraph, person1, person3);
// {Joan, Clarke}, {Bletchey Park, Cambridge} <-> {Grace, Hopper}, {New York} = -1
        printRelationshipLevel(personsGraph, person2, person3);

        System.out.println("Home Assignment Ended");
    }


    public static void printRelationshipLevel(PersonsGraph personsGraph, Person personA, Person personB) {
        int level = personsGraph.findMinRelationLevel(personA, personB);
        LOG.info("Relation between " + personA + " to " + personB + " is " + level);
    }

    // - Initialization of the utility with person instances.
    public void init(Person[] people) {
        // Assumption - there aren't two people with the same name and address.
        for (Person person : people) {
            personsGraph.put(person, new HashSet<>());

            Address address = person.getAddress();
            Set<Person> relativesByAddressSet = personsByAddress.computeIfAbsent(address, k -> new HashSet<>());
//            if (relativesByAddressSet == null) {
//                relativesByAddressSet = new HashSet<>();
//                personsByName.put(fullName, relativesByAddressSet);
//            }
            relativesByAddressSet.add(person);
            addToFirstLevelRelativePeople(person, relativesByAddressSet);

            Name fullName = person.getFullName();
            Set<Person> relativesByNameSet = personsByName.computeIfAbsent(fullName, k -> new HashSet<>());
            relativesByNameSet.add(person);
            addToFirstLevelRelativePeople(person, relativesByNameSet);

        }

    }

    // Add relative persons
    private void addToFirstLevelRelativePeople(Person person, Set<Person> personsBySet) {
        LOG.info("Adding " + person + " to Graph");
        for (Person firstLevelRelativePerson : personsBySet) {
            if (!firstLevelRelativePerson.equals(person)) {
                Set<Person> firstLevelRelativePeople = personsGraph.get(firstLevelRelativePerson);
                if (!firstLevelRelativePeople.contains(person)) {
                    firstLevelRelativePeople.add(person);
                    LOG.info(" ADD: " +person + " Added to " + firstLevelRelativePerson + " first level Relatives");
                }
                else {
                    LOG.info(" Exist: " + person + " already contains in " + firstLevelRelativePerson + " first level Relatives");
                }
            }

        }
    }

    // - Returns the minimal level of relation between personA and personB. If they are not related, return -1.
    public int findMinRelationLevel(Person personA, Person personB) {
        int level = -1;

        Set<Person> visitedSet = new HashSet<>();
        Set<Person> relativesSet = personsGraph.get(personA);
        if (personA.equals(personB)) {
            level = 0;
        }
        else {
            int digInGraph = digInGraph(personB, relativesSet, visitedSet, null);
            level = digInGraph > 0 ? digInGraph : -1;
        }

        return level;
    }

    // Dijkstra Shortest Path Algorithm
    private int digInGraph(Person personB, Set<Person> relativesSet, Set<Person> visitedSet, Person startPerson) {
        Map<Person, Integer> startPersons2LevelsMap = new HashMap<>();
        int level = 0;
        if (relativesSet.contains(personB)) {
            level = 1;
        }
        else {
            for (Person person : relativesSet) {
                if (!visitedSet.contains(person)) {
                    Set<Person> nextRelativesSet = personsGraph.get(person);
                    level += digInGraph(personB, nextRelativesSet, visitedSet, startPerson == null ? person : startPerson);
                    visitedSet.add(person);
                    if (startPerson == null) {
                        startPersons2LevelsMap.put(person, level > 0 ? level + 1 : 0);
                        level = 0;
                    }
                }
            }
        }

        if (startPerson == null && !startPersons2LevelsMap.isEmpty()) {
            int bestLevel = Integer.MAX_VALUE;
            for (int levelInMap : startPersons2LevelsMap.values()) {
                bestLevel = Math.min(levelInMap, bestLevel);
            }
            level = bestLevel;
        }
        return level;
    }

    @Override
    public String toString() {
        return "PersonsGraph = " + personsGraph;
    }

}
