package org.hibernate.tutorial;

import org.hibernate.Session;

import java.util.*;

import org.hibernate.tutorial.domain.Event;
import org.hibernate.tutorial.domain.Person;
import org.hibernate.tutorial.util.HibernateUtil;

// inside IntelliJ, I got some errors such as
// java.sql.SQLException: socket creation error
// Note in the pom.xml I made some change to pass the mvn compile error
//
// <!--<dependency>
//            <groupId>hsqldb</groupId>
//            <artifactId>hsqldb</artifactId>
//            <version>1.8.1.2</version>
//        </dependency>-->
//        <dependency>
//            <groupId>com.google.code.maven-play-plugin.hsqldb</groupId>
//            <artifactId>hsqldb</artifactId>
//            <version>1.8.1.2</version>
//        </dependency>
//
// https://stackoverflow.com/questions/20278854/getting-exception-java-sql-sqlexception-socket-creation-error
//
// I did this tutorial a bit over 10 years ago
// https://www.stlplace.com/2010/03/28/some-corrections-to-hibernate-tutorial/
//
public class EventManager {

    public static void main(String[] args) {
        EventManager mgr = new EventManager();

        if (args[0].equals("store")) {
            mgr.createAndStoreEvent("My Event", new Date());
        }
        else if (args[0].equals("list")) {
            List events = mgr.listEvents();
            for (int i = 0; i < events.size(); i++) {
                Event theEvent = (Event) events.get(i);
                System.out.println(
                        "Event: " + theEvent.getTitle() + " Time: " + theEvent.getDate()
                );
            }
        }
        else if (args[0].equals("addpersontoevent")) {
            Long eventId = mgr.createAndStoreEvent("My Event", new Date());
            Long personId = mgr.createAndStorePerson("Foo", "Bar");
            mgr.addPersonToEvent(personId, eventId);
            System.out.println("Added person " + personId + " to event " + eventId);
        }
        else if (args[0].equals("addemailtoperson")) {  
            Long personId = mgr.createAndStorePerson("Major", "Xu");
            String emailAddress = "major_xu@yahoo.com";
            mgr.addEmailToPerson(personId, emailAddress);
            //mgr.addPersonToEvent(personId, eventId);
            System.out.println("Added email " + emailAddress + " to person " + personId);
        }

        HibernateUtil.getSessionFactory().close();
    }

    private Long createAndStoreEvent(String title, Date theDate) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Event theEvent = new Event();
        theEvent.setTitle(title);
        theEvent.setDate(theDate);
        Long eventID = (Long) session.save(theEvent);

        session.getTransaction().commit();
        return eventID;
    }
    
    private Long createAndStorePerson(String firstname, String lastname) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person thePerson = new Person();
        thePerson.setFirstname(firstname);
        thePerson.setLastname(lastname);
        Long personID = (Long) session.save(thePerson);

        session.getTransaction().commit();
        return personID;
    }
    
    private List listEvents() {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();
        List result = session.createQuery("from Event").list();
        session.getTransaction().commit();
        return result;
    }
    
    private void addPersonToEvent(Long personId, Long eventId) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person) session.load(Person.class, personId);
        Event anEvent = (Event) session.load(Event.class, eventId);
        aPerson.getEvents().add(anEvent);

        session.getTransaction().commit();
    }
    
    private void addEmailToPerson(Long personId, String emailAddress) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        session.beginTransaction();

        Person aPerson = (Person) session.load(Person.class, personId);
        // adding to the emailAddress collection might trigger a lazy load of the collection
        aPerson.getEmailAddresses().add(emailAddress);

        session.getTransaction().commit();
    }

}