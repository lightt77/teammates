package teammates.test.cases.webapi;

import java.util.List;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.util.Const;
import teammates.logic.core.CoursesLogic;
import teammates.ui.webapi.action.BinCourseAction;
import teammates.ui.webapi.action.JsonResult;
import teammates.ui.webapi.output.CourseData;

/**
 * SUT: {@link BinCourseAction}.
 */
public class BinCourseActionTest extends BaseActionTest<BinCourseAction> {

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.BIN_COURSE;
    }

    @Override
    protected String getRequestMethod() {
        return PUT;
    }

    @Override
    @Test
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        loginAsInstructor(instructorId);

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case, 2 courses. Expect 1 to be binned and 1 to stay.");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        CourseAttributes courseToBeDeleted = logic.getCourse(instructor1OfCourse1.getCourseId());

        logic.createCourseAndInstructor(instructorId, "icdct.tpa.id1", "New course", "UTC");

        BinCourseAction binCourseAction = getAction(submissionParams);
        JsonResult result = getJsonResult(binCourseAction);
        CourseData courseData = (CourseData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyCourseData(courseData, courseToBeDeleted.getId(), courseToBeDeleted.getName(),
                courseToBeDeleted.getTimeZone().getId());

        List<CourseAttributes> courseList = logic.getCoursesForInstructor(instructorId);
        assertEquals(1, courseList.size());
        assertEquals("icdct.tpa.id1", courseList.get(0).getId());

        assertNotNull(logic.getCourse(instructor1OfCourse1.courseId).deletedAt);

        ______TS("Masquerade mode, delete last course");

        loginAsAdmin();

        submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdct.tpa.id1",
        };

        binCourseAction = getAction(addUserIdToParams(instructorId, submissionParams));
        result = getJsonResult(binCourseAction);

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        courseData = (CourseData) result.getOutput();

        verifyCourseData(courseData, "icdct.tpa.id1", "New course", "UTC");
        assertNotNull(logic.getCourse("icdct.tpa.id1").deletedAt);
    }

    @Test
    protected void testExecute_nonExistentCourse_shouldFail() {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "fake-course",
        };

        assertNull(logic.getCourse("fake-course"));

        EntityNotFoundException e = assertThrows(EntityNotFoundException.class, () ->
                getAction(submissionParams).execute());
        assertEquals("Trying to update a Course that doesn't exist: ", e.getMessage());
    }

    @Test
    protected void testExecute_courseAlreadyBin_shouldFail() throws Exception {
        InstructorAttributes instructor1OfCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        String instructorId = instructor1OfCourse1.googleId;

        loginAsInstructor(instructorId);

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, instructor1OfCourse1.courseId,
        };

        logic.moveCourseToRecycleBin(instructor1OfCourse1.courseId);
        CourseAttributes courseInformation = logic.getCourse(instructor1OfCourse1.courseId);
        assertNotNull(courseInformation.deletedAt);

        BinCourseAction binCourseAction = getAction(submissionParams);
        JsonResult result = getJsonResult(binCourseAction);
        CourseData courseData = (CourseData) result.getOutput();

        assertEquals(HttpStatus.SC_OK, result.getStatusCode());
        verifyCourseData(courseData, courseInformation.getId(), courseInformation.getName(),
                courseInformation.getTimeZone().getId());
    }

    private void verifyCourseData(CourseData data, String courseId, String courseName, String timeZone) {
        assertEquals(data.getCourseId(), courseId);
        assertEquals(data.getCourseName(), courseName);
        assertEquals(data.getTimeZone(), timeZone);
    }

    @Override
    @Test
    protected void testAccessControl() throws Exception {
        logic.createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse1").googleId,
                "icdat.owncourse", "New course", "UTC");

        String[] submissionParams = new String[] {
                Const.ParamsNames.COURSE_ID, "icdat.owncourse",
        };

        /*  Test access for users
         *  This should be separated from testing for admin as we need to recreate the course after being removed
         */
        verifyInaccessibleWithoutLogin(submissionParams);
        verifyInaccessibleForUnregisteredUsers(submissionParams);
        verifyInaccessibleForStudents(submissionParams);
        verifyInaccessibleForInstructorsOfOtherCourses(submissionParams);
        verifyInaccessibleWithoutModifyCoursePrivilege(submissionParams);
        verifyAccessibleForInstructorsOfTheSameCourse(submissionParams);

        CoursesLogic.inst().deleteCourseCascade("icdat.owncourse");

        /* Test access for admin in masquerade mode */
        logic.createCourseAndInstructor(
                typicalBundle.instructors.get("instructor1OfCourse1").googleId,
                "icdat.owncourse", "New course", "UTC");
        verifyAccessibleForAdminToMasqueradeAsInstructor(submissionParams);

        CoursesLogic.inst().deleteCourseCascade("icdat.owncourse");
    }

}
