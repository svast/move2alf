package eu.xenit.move2alf.logic;

import org.hibernate.Query;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by willem on 3/21/16.
 */
@Service("statsService")
public class StatsServiceImpl extends AbstractHibernateService implements StatsService{
    @Override
    public List getTotals() {
        final Query query = sessionFactory
            .getCurrentSession()
            .createQuery(
                    "select d.status, count(*) from ProcessedDocument as d group by d.status");
        return query.list();
    }

    @Override
    public List getTotalsPerJob() {
        final Query query = sessionFactory
                .getCurrentSession()
                .createQuery(
                        "select j.name, d.status, count(j.id) as n " +
                                "from ProcessedDocument as d " +
                                "inner join d.cycle as c " +
                                "inner join c.job as j " +
                                "group by j.id, d.status " +
                                "order by j.name");
        return query.list();
    }

}