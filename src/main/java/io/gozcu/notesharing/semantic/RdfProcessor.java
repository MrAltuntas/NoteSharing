package io.gozcu.notesharing.semantic;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.riot.RDFParser;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class RdfProcessor {

    /**
     * Validate an RDF file
     * @param filePath path to the file to be validated
     * @return true if validation is successful
     */
    public boolean validate(String filePath) {
        try {
            // Create model
            Model model = ModelFactory.createDefaultModel();

            // Try to read the file - syntax errors will throw exceptions
            RDFParser.source(filePath).lang(Lang.TURTLE).parse(model);

            System.out.println("File successfully validated: " + filePath);
            System.out.println("Total number of triples: " + model.size());
            return true;
        } catch (Exception e) {
            System.err.println("Validation error - " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Convert RDF file to JSON-LD format
     * @param inputFile input file (in Turtle format)
     * @param outputFile output file (in JSON-LD format)
     */
    public void convertToJsonLd(String inputFile, String outputFile) {
        try {
            // Create model and load Turtle file
            Model model = ModelFactory.createDefaultModel();
            model.read(new FileInputStream(inputFile), null, "TURTLE");

            // Save as JSON-LD
            RDFDataMgr.write(
                    new FileOutputStream(outputFile),
                    model,
                    RDFFormat.JSONLD
            );

            System.out.println("Conversion completed: " + inputFile + " -> " + outputFile);
        } catch (IOException e) {
            System.err.println("Conversion error: " + e.getMessage());
        }
    }

    /**
     * Convert RDF file to RDF/XML format
     * @param inputFile input file (in Turtle format)
     * @param outputFile output file (in RDF/XML format)
     */
    public void convertToRdfXml(String inputFile, String outputFile) {
        try {
            // Create model and load Turtle file
            Model model = ModelFactory.createDefaultModel();
            model.read(new FileInputStream(inputFile), null, "TURTLE");

            // Save as RDF/XML
            RDFDataMgr.write(
                    new FileOutputStream(outputFile),
                    model,
                    RDFFormat.RDFXML
            );

            System.out.println("Conversion completed: " + inputFile + " -> " + outputFile);
        } catch (IOException e) {
            System.err.println("Conversion error: " + e.getMessage());
        }
    }

    /**
     * Main method - for testing
     */
    public static void main(String[] args) {
        RdfProcessor processor = new RdfProcessor();

        // Create file paths - assuming they are in the project root directory
        Path currentPath = Paths.get("").toAbsolutePath();
        String ontologyFile = currentPath.resolve("ontology/ontology.ttl").toString();
        String instancesFile = currentPath.resolve("ontology/instances.ttl").toString();

        // Validate files
        System.out.println("VALIDATION PROCESS:");
        processor.validate(ontologyFile);
        processor.validate(instancesFile);

        // Convert to JSON-LD format
        System.out.println("\nJSON-LD CONVERSION PROCESS:");
        processor.convertToJsonLd(ontologyFile, "ontology/ontology.jsonld");
        processor.convertToJsonLd(instancesFile, "ontology/instances.jsonld");

        // Convert to RDF/XML format
        System.out.println("\nRDF/XML CONVERSION PROCESS:");
        processor.convertToRdfXml(ontologyFile, "ontology/ontology.rdf");
        processor.convertToRdfXml(instancesFile, "ontology/instances.rdf");
    }
}