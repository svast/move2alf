package eu.xenit.move2alf.core.simpleaction;

import eu.xenit.move2alf.core.ReportMessage;
import eu.xenit.move2alf.core.action.ClassInfo;
import eu.xenit.move2alf.core.action.Move2AlfReceivingAction;
import eu.xenit.move2alf.core.action.StartCycleAction;
import eu.xenit.move2alf.core.dto.Cycle;
import eu.xenit.move2alf.core.dto.ProcessedDocument;
import eu.xenit.move2alf.core.dto.ProcessedDocumentParameter;
import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import eu.xenit.move2alf.logic.JobService;
import eu.xenit.move2alf.logic.JobServiceImpl;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

@ClassInfo(classId = "SAReport",
            description = "Writes report messages to the database")
public class SAReport extends Move2AlfReceivingAction<List<ReportMessage>>{

    private static final Logger logger = LoggerFactory.getLogger(SAReport.class);

	public String getDescription() {
		return "Checking for errors";
	}

    @Autowired
    private SessionFactory sessionFactory;

    @Autowired
    private JobService jobService;

    @Override
    public void executeImpl(List<ReportMessage> batch) {
        logger.debug("Saving processedDocuments in database. Number of processedDocuments: "+batch.size());
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        Cycle cycle = getLastCycle(session);


        for(ReportMessage reportMessage: batch){
            ProcessedDocument processedDocument = new ProcessedDocument();
            processedDocument.setCycle(cycle);
            processedDocument.setName(reportMessage.name);
            processedDocument.setProcessedDateTime(reportMessage.date);
            processedDocument.setStatus(EProcessedDocumentStatus.valueOf(reportMessage.state.toUpperCase()));
            for (final ProcessedDocumentParameter param : reportMessage.params) {
                if (param.getValue().length() > 255) {
                    param.setValue(param.getValue().substring(0, 255));
                }
            }
            processedDocument.setProcessedDocumentParameterSet(reportMessage.params);
            processedDocument.setReference(reportMessage.reference);
            session.save(processedDocument);
        }
        session.flush();
        session.clear();
        tx.commit();
        session.close();

    }

    public Cycle getLastCycle(Session session) {
        return (Cycle) session.createQuery("from Cycle as c where c.job.name=? order by c.startDateTime desc limit 1").setString(0, stateContext.getJobId()).list().get(0);
    }
}
