package com.example.demo.utils;


import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.formats.OWLXMLDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLAxiomChange;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLDisjointClassesAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.Set;

@Component
@SuppressWarnings("all")
public class OntologyHelper {
    OWLOntologyManager m = OWLManager.createOWLOntologyManager();
    OWLDataFactory df = OWLManager.getOWLDataFactory();

    public IRI convertStringToIRI(String ns) {
        return IRI.create(ns);
    }


    public OWLOntology createOntology(String iri) throws OWLOntologyCreationException {
        return createOntology(convertStringToIRI(iri));
    }

    public OWLOntology createOntology(IRI iri) throws OWLOntologyCreationException {
        return m.createOntology(iri);
    }



    /**
     * 删除当前的本体
     *
     * @param o
     */
    public void removeOntology(OWLOntology o) {
        m.removeOntology(o);
    }

    //保存及修改
    public void writeOntology(OWLOntology o, File file, String suffix) throws OWLOntologyStorageException {
        m.containsVersion(IRI.create("1.0"));
        if (suffix.equals("ttl")) {
            m.saveOntology(o, new TurtleDocumentFormat(), IRI.create(file.toURI()));
        } else if (suffix.equals("owl")) {
            m.saveOntology(o, new OWLXMLDocumentFormat(), IRI.create(file.toURI()));
        } else {
            m.saveOntology(o, IRI.create(file.toURI()));
        }
        m.removeOntology(o);
        m.clearOntologies();
    }

    public OWLOntology readOntology(File source)
            throws OWLOntologyCreationException {
        return m.loadOntologyFromOntologyDocument(source);
    }


    public OWLClass createClass(String iri) {
        return createClass(convertStringToIRI(iri));
    }

    public OWLClass createClass(IRI iri) {
        return df.getOWLClass(iri);
    }



    public OWLAxiomChange addClass(OWLOntology o, OWLClass owlClass) {
        return new AddAxiom(o, df.getOWLDeclarationAxiom(owlClass));
    }

    public OWLAxiomChange addClassLabel(OWLOntology o, Object label, OWLClass owlClass) {
        OWLAnnotationProperty rdfsLabel = df.getRDFSLabel();
        OWLLiteral owlLiteral = null;
        if (label instanceof Integer) {
            owlLiteral = df.getOWLLiteral(Integer.parseInt(label.toString()));
        } else if (label instanceof Double) {
            owlLiteral = df.getOWLLiteral(Double.parseDouble(label.toString()));
        } else if (label instanceof Boolean) {
            owlLiteral = df.getOWLLiteral(Boolean.parseBoolean(label.toString()));
        } else {
            owlLiteral = df.getOWLLiteral(label.toString());
        }
        OWLAnnotation owlAnnotation = df.getOWLAnnotation(rdfsLabel, owlLiteral);

        return new AddAxiom(o, df.getOWLAnnotationAssertionAxiom(owlClass.getIRI(), owlAnnotation));
    }


    public OWLAxiomChange createSubclass(OWLOntology o, OWLClass subclass, OWLClass superclass) {
        return new AddAxiom(o, df.getOWLSubClassOfAxiom(subclass, superclass));
    }

    public void removeAxioms(OWLOntology o, Set<OWLAxiom> owlAxioms) {
        m.removeAxioms(o, owlAxioms.stream());
    }

    public void applyChange(OWLAxiomChange... axiom) {
        applyChanges(axiom);
    }

    private void applyChanges(OWLAxiomChange... axioms) {
        m.applyChanges(Arrays.asList(axioms));
    }

    public OWLIndividual createIndividual(String iri) {
        return createIndividual(convertStringToIRI(iri));
    }

    private OWLIndividual createIndividual(IRI iri) {
        return df.getOWLNamedIndividual(iri);
    }

    public OWLAxiomChange associateIndividualWithClass(OWLOntology o,
                                                       OWLClass clazz,
                                                       OWLIndividual individual) {
        return new AddAxiom(o, df.getOWLClassAssertionAxiom(clazz, individual));
    }

    public OWLObjectProperty createObjectProperty(String iri) {
        return createObjectProperty(convertStringToIRI(iri));
    }

    public OWLObjectProperty createObjectProperty(IRI iri) {
        return df.getOWLObjectProperty(iri);
    }

    /**
     * With ontology o, property in refHolder points to a refTo.
     *
     * @param o         The ontology reference
     * @param property  the data property reference
     * @param refHolder the container of the property
     * @param refTo     the class the property points to
     * @return a patch to the ontology
     */
    public OWLAxiomChange associateObjectPropertyWithClass(OWLOntology o,
                                                           OWLObjectProperty property,
                                                           OWLClass refHolder,
                                                           OWLClass refTo) {
        OWLClassExpression hasSomeRefTo = df.getOWLObjectSomeValuesFrom(property, refTo);
        OWLSubClassOfAxiom ax = df.getOWLSubClassOfAxiom(refHolder, hasSomeRefTo);
        return new AddAxiom(o, ax);
    }

    /**
     * With ontology o, an object of class a cannot be simultaneously an object of class b.
     * This is not implied to be an inverse relationship; saying that a cannot be a b does not
     * mean that b cannot be an a.
     *
     * @param o the ontology reference
     * @param a the source of the disjunction
     * @param b the object of the disjunction
     * @return a patch to the ontology
     */
    public OWLAxiomChange addDisjointClass(OWLOntology o, OWLClass a, OWLClass b) {
        OWLDisjointClassesAxiom expression = df.getOWLDisjointClassesAxiom(a, b);
        return new AddAxiom(o, expression);
    }

    public OWLAxiomChange addObjectProperty(OWLOntology o, OWLIndividual target, OWLObjectProperty property, OWLIndividual value) {
        OWLObjectPropertyAssertionAxiom prop = df.getOWLObjectPropertyAssertionAxiom(property, target, value);
        return new AddAxiom(o, prop);
    }


    public OWLDataProperty createDataProperty(String iri) {
        return createDataProperty(convertStringToIRI(iri));
    }

    public OWLDataProperty createDataProperty(IRI iri) {
        return df.getOWLDataProperty(iri);
    }

    public OWLAxiomChange addDataPropertyLabel(OWLOntology o, OWLDataProperty dataProperty, String label) {
        OWLAnnotation owlAnnotation = df.getOWLAnnotation(df.getRDFSLabel(), df.getOWLLiteral(label));
        return new AddAxiom(o, df.getOWLAnnotationAssertionAxiom(dataProperty.getIRI(), owlAnnotation));
    }

    /**
     * 数据属性所属的类
     * @param o
     * @param dataProperty
     * @param owlClass
     * @return
     */
    public OWLAxiomChange addDataPropertyDomain(OWLOntology o, OWLDataProperty dataProperty, OWLClass owlClass) {
        return new AddAxiom(o, df.getOWLDataPropertyDomainAxiom(dataProperty, owlClass));
    }

    /**
     * 数据属性所属的数据类型
     * @param o
     * @param dataProperty
     * @param dataType : boolean/double/float/integer/string
     * @return
     */
    public OWLAxiomChange addDataPropertyRange(OWLOntology o, OWLDataProperty dataProperty, String dataType) {
        OWLDatatype owlDatatype=null;
        if (dataType.equals("boolean")){
            owlDatatype=df.getBooleanOWLDatatype();
        }else if (dataType.equals("double")){
            owlDatatype=df.getDoubleOWLDatatype();
        }else if (dataType.equals("float")){
            owlDatatype=df.getFloatOWLDatatype();
        }else if (dataType.equals("integer")){
            owlDatatype= df.getIntegerOWLDatatype();
        }else {
            owlDatatype= df.getStringOWLDatatype();
        }
        return new AddAxiom(o, df.getOWLDataPropertyRangeAxiom(dataProperty, owlDatatype ));
    }
    /**
     * 添加实例数据
     *
     * @param o
     * @param individual
     * @param property
     * @param value
     * @return
     */
    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual, OWLDataProperty property, String value) {
        OWLLiteral literal = df.getOWLLiteral(value, OWL2Datatype.XSD_STRING);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }

    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual, OWLDataProperty property, boolean value) {
        OWLLiteral literal = df.getOWLLiteral(value);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }

    public OWLAxiomChange addDataToIndividual(OWLOntology o, OWLIndividual individual, OWLDataProperty property, int value) {
        OWLLiteral literal = df.getOWLLiteral(value);
        return new AddAxiom(o, df.getOWLDataPropertyAssertionAxiom(property, individual, literal));
    }


}
