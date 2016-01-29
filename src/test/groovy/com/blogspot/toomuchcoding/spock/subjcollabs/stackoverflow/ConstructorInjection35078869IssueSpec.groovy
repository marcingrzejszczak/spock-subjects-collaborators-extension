package com.blogspot.toomuchcoding.spock.subjcollabs.stackoverflow

import spock.lang.Specification
import com.blogspot.toomuchcoding.spock.subjcollabs.Collaborator
import com.blogspot.toomuchcoding.spock.subjcollabs.Subject
import spock.lang.Issue

@Issue("http://stackoverflow.com/questions/35078869/exception-while-using-spock-for-java-unit-testing/35079024#35079024")
class ConstructorInjection35078869IssueSpec extends Specification {

	@Collaborator
	UserDAO userDAO = Mock()

	@Subject
	// WILL FAIL:
	// UserService userService
	UserServiceImpl userService

	def "should delete a user"() {
		given: "given a user to delete"
			long id = 1L;
		when:
			userService.deleteUser(id);
		then:
			true
	}

	public interface GenericDAO<E, PK extends Serializable> {

		void persist(E entity);

		void delete(E entity);

		E findById(PK primaryKey);

		E findOne(Object query);

		List<E> findMany(Object query);

		List<E> findAll();
	}

	public interface UserService {
		public void deleteUser(Long userId);
	}

	public interface UserDAO extends GenericDAO<Object, Serializable> {
		Object findUserByEmail(String email);
	}

	public class PooledStringDigester {}

	public class SecurityUtil {

		private PooledStringDigester _stringDigester;

		public String encryptUserPassword(String originalPassword) {
			return ""
		}
	}

	public class UserServiceImpl implements UserService {

		private UserDAO userDAO;

		private SecurityUtil securityUtil;

		@Override
		public void deleteUser(Long userId) {
			Object user = userDAO.findById(userId);
			userDAO.delete(user);
		}

	}
}
