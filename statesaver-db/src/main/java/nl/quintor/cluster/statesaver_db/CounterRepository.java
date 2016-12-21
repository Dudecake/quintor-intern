package nl.quintor.cluster.statesaver_db;

import java.util.List;

import javax.persistence.LockModeType;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface CounterRepository extends CrudRepository<Count, Long>
{
	// boolean exists(String instanceId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	List<Count> findBySessionId(String sessionId);

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	Count findOneBySessionId(String sessionId);
}
