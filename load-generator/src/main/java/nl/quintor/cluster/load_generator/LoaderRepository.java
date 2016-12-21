package nl.quintor.cluster.load_generator;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import nl.quintor.cluster.load_generator.Instance;

public interface LoaderRepository extends CrudRepository<Instance, Long>
{
	List<Instance> findByInstanceId(String instanceId);
	
	Instance findOneByInstanceId(String instanceId);
}
