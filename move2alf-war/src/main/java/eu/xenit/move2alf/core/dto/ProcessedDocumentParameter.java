package eu.xenit.move2alf.core.dto;

public class ProcessedDocumentParameter {
    public ProcessedDocument getProcessedDocument() {
        return processedDocument;
    }

    public void setProcessedDocument(ProcessedDocument processedDocument) {
        this.processedDocument = processedDocument;
    }

    private ProcessedDocument processedDocument;

	private String name;

	private String value;

	public ProcessedDocumentParameter() {

	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

}
