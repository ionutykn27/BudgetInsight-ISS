module org.example.budgetinsight {
    requires javafx.controls;
    requires javafx.fxml;
    requires atlantafx.base;
    requires org.controlsfx.controls;
    requires org.hibernate.orm.core;
    requires org.hibernate.commons.annotations;
    requires jakarta.persistence;
    requires java.sql;
    requires java.naming;
    requires jakarta.transaction;
    requires jakarta.cdi;
    requires jakarta.xml.bind;
    requires org.jboss.logging;
    requires com.fasterxml.classmate;
    requires net.bytebuddy;

    uses jakarta.persistence.spi.PersistenceProvider;

    opens org.example.budgetinsight to javafx.fxml;
    opens org.example.budgetinsight.model to org.hibernate.orm.core;

    exports org.example.budgetinsight;
    exports org.example.budgetinsight.model;
}