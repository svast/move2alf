package eu.xenit.move2alf.logic;

import eu.xenit.move2alf.core.enums.EProcessedDocumentStatus;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

/**
 * Created by willem on 3/21/16.
 */
@Service("statsService")
public class StatsServiceImpl extends AbstractHibernateService implements StatsService{

    @Override
    public long getTotalsSucceeded() {
        final Query query = sessionFactory
        .getCurrentSession()
        .createQuery(
                "select count(*) from ProcessedDocument as d where d.status = ?")
            .setParameter(0, EProcessedDocumentStatus.OK);
        return (Long)query.uniqueResult();
    }

}