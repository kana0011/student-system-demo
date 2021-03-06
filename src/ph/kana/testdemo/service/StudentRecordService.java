package ph.kana.testdemo.service;

import java.util.List;
import ph.kana.testdemo.exception.DataAccessException;
import ph.kana.testdemo.exception.ServiceException;
import ph.kana.testdemo.exception.SubjectRequirementException;
import ph.kana.testdemo.model.Student;
import ph.kana.testdemo.model.StudentRecord;
import ph.kana.testdemo.model.Subject;
import ph.kana.testdemo.repository.StudentRecordRepository;

class StudentRecordService {

	private SubjectService subjectService;
	private StudentRecordRepository studentRecordRepository;

	public void setSubjectService(SubjectService subjectService) {
		this.subjectService = subjectService;
	}

	public void setStudentRecordRepository(StudentRecordRepository studentRecordRepository) {
		this.studentRecordRepository = studentRecordRepository;
	}

	public void enroll(Student student, List<Subject> subjects) throws ServiceException {
		try {
			for (Subject subject : subjects) {
				List<Subject> preRequisites = subjectService.fetchPrerequisites(subject);
				checkPassedAllPreRequisites(student, preRequisites);

				enrollToSubject(student, subject);
			}
		} catch (DataAccessException | SubjectRequirementException e) {
			throw new ServiceException("Error in enrollment", e);
		}
	}

	private void checkPassedAllPreRequisites(Student student, List<Subject> preRequisites) throws DataAccessException {
		for (Subject preRequisite : preRequisites) {
			StudentRecord record = studentRecordRepository
				.findByStudentAndSubject(student, preRequisite);
			if (record == null || !record.isPassed()) {
				throw new SubjectRequirementException("Pre-requiste not passed yet.");
			}
		}
	}

	private StudentRecord enrollToSubject(Student student, Subject subject) throws DataAccessException {
		StudentRecord enrollRecord = new StudentRecord();
		enrollRecord.setStudent(student);
		enrollRecord.setSubject(subject);
		studentRecordRepository.save(enrollRecord);

		return enrollRecord;
	}
}
