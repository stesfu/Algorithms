package csp;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * CSP: Calendar Satisfaction Problem Solver Provides a solution for scheduling
 * some n meetings in a given period of time and according to some set of unary
 * and binary constraints on the dates of each meeting.
 */
public class CSP {

    /**
     * Public interface for the CSP solver in which the number of meetings, range of
     * allowable dates for each meeting, and constraints on meeting times are
     * specified.
     * 
     * @param nMeetings   The number of meetings that must be scheduled, indexed
     *                    from 0 to n-1
     * @param rangeStart  The start date (inclusive) of the domains of each of the n
     *                    meeting-variables
     * @param rangeEnd    The end date (inclusive) of the domains of each of the n
     *                    meeting-variables
     * @param constraints Date constraints on the meeting times (unary and binary
     *                    for this assignment)
     * @return A list of dates that satisfies each of the constraints for each of
     *         the n meetings, indexed by the variable they satisfy, or null if no
     *         solution exists.
     */
    public static List<LocalDate> solve(int nMeetings, LocalDate rangeStart, LocalDate rangeEnd,
            Set<DateConstraint> constraints) {
        List<LocalDate> solved = new ArrayList<LocalDate>();
        Set<LocalDate> meetingDomain = getDomain(rangeStart, rangeEnd);
        List<DateVar> meetings = generateDatePossibilities(nMeetings, meetingDomain, constraints);

        if (emptyDomainExists(meetings)) {
            return null;
        }
        nodePreprocessing(meetings, constraints);
       // ac3Preprocessing(meetings, constraints);

        return backtracking(nMeetings, meetings, solved, constraints, 0);
    }

    /**
     * A class to keep track of the meeting index, current date assignment, and
     * allowable domain of meeting times
     *
     */
    private static class DateVar {
        private int index;
        private LocalDate current;
        Set<LocalDate> domain;

        /**
         * Constructs a date variable assignment for a meeting
         * 
         * @param index  meeting index based on desired nMeeting
         * @param date   the current assigned date of meeting
         * @param domain A set of allowable days the meeting can be on
         */
        DateVar(int index, LocalDate date, Set<LocalDate> domain) {
            this.index = index;
            this.current = date;
            this.domain = domain;
        }
    }

    private static class Arc {
        private int head;
        private int tail;

        Arc(int head, int tail) {
            this.head = head;
            this.tail = tail;
        }
    }

    /**
     * Given a range start and end, determine a set of days where the meetings can
     * be held
     * 
     * @param rangeStart The start date (inclusive) of the domains of each of the n
     *                   meeting-variables
     * @param rangeEnd   The end date (inclusive) of the domains of each of the n
     *                   meeting-variables
     * @return set of allowable LocalDates
     */
    private static Set<LocalDate> getDomain(LocalDate rangeStart, LocalDate rangeEnd) {
        Set<LocalDate> newDomain = rangeStart.datesUntil(rangeEnd).collect(Collectors.toSet());
        newDomain.add(rangeEnd);
        return newDomain;
    }

    /**
     * Checks to see whether any of the meetings has an empty domain based on
     * constraints
     * 
     * @param meetings list of all DateVar meetings
     * @return boolean whether an empty domain exists
     */
    private static boolean emptyDomainExists(List<DateVar> meetings) {
        for (DateVar meeting : meetings) {
            if (meeting.domain.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Given a range of dates and number of meetings, generates needed, desired
     * meetings
     * 
     * @param nMeetings   The number of meetings that must be scheduled, indexed
     *                    from 0 to n-1
     * @param domain      set of allowable LocalDates for assignment
     * @param constraints Date constraints on the meeting times (unary and binary
     *                    for this assignment)
     * @return
     */
    private static List<DateVar> generateDatePossibilities(int nMeetings, Set<LocalDate> domain,
            Set<DateConstraint> constraints) {
        List<DateVar> possibilities = new ArrayList<DateVar>();

        for (int i = 0; i < nMeetings; i++) {
            possibilities.add(new DateVar(i, null, domain));
        }
        return possibilities;
    }

    /**
     * Assigns allowable dates to desired number of meetings without violating
     * constraints
     * 
     * @param nMeetings   The number of meetings that must be scheduled, indexed
     *                    from 0 to n-1
     * @param meetings    list of all DateVar meetings
     * @param assignement List of meeting assigned local dates
     * @param constraints Date constraints on the meeting times (unary and binary
     *                    for this assignment)
     * @param assignIndex meeting index of current assignment
     * @return
     */
    private static List<LocalDate> backtracking(int nMeetings, List<DateVar> meetings, List<LocalDate> assignment,
            Set<DateConstraint> constraints, int assignIndex) {

        if (assignIndex == nMeetings && checkValidAssignment(assignment, constraints)) {
            return assignment;
        }

        for (LocalDate date : meetings.get(assignIndex).domain) {
            if (date != null && assignment.size() < nMeetings) {
                assignment.add(date);
                meetings.get(assignIndex).current = date;
            }
            if (checkValidAssignment(assignment, constraints)) {
                List<LocalDate> meetingAssignments = backtracking(nMeetings, meetings, assignment, constraints,
                        assignIndex + 1);
                if (meetingAssignments != null) {
                    return meetingAssignments;
                }
            }

            meetings.get(assignIndex).current = null;
            if (assignment.contains(date) && assignment.get(assignIndex).isEqual(date)) {
                assignment.remove(assignIndex);
            }

        }
        return null;
    }

    /**
     * Given an assigned list of local dates, checks if every meeting assignment is
     * valid given the total constraints
     * 
     * @param assignedMeetings List of meeting assigned local dates
     * @param constraints      Date constraints specified in solve call
     * @return boolean whether assignment is valid
     */
    private static boolean checkValidAssignment(List<LocalDate> assignedMeetings, Set<DateConstraint> constraints) {
        BinaryDateConstraint dateConstraint;

        if (assignedMeetings != null) {
            for (DateConstraint constraint : constraints) {
                if (constraint.arity() == 2) {
                    dateConstraint = (BinaryDateConstraint) constraint;
                    if (dateConstraint.L_VAL < assignedMeetings.size()
                            && dateConstraint.R_VAL < assignedMeetings.size()) {
                        if (assignedMeetings.get(dateConstraint.L_VAL) == null
                                || assignedMeetings.get(dateConstraint.R_VAL) == null) {
                            return true;
                        }
                        if (!checkConsistency(assignedMeetings.get(dateConstraint.L_VAL),
                                assignedMeetings.get(dateConstraint.R_VAL), constraint)) {
                            return false;
                        }
                    }

                }
            }
        }
        return true;
    }

    /**
     * Checks all unary constraints and removes it from the domain of a date var if
     * it violates an established constraint
     * 
     * @param meetings
     * @param constraints
     */
    private static void nodePreprocessing(List<DateVar> meetings, Set<DateConstraint> constraints) {
        UnaryDateConstraint dateConstraint;
        Set<LocalDate> violatedDates = new HashSet<LocalDate>();

        for (DateVar meeting : meetings) {
            for (DateConstraint constraint : constraints) {
                for (LocalDate date : meeting.domain) {
                    if (constraint.arity() == 1) {
                        dateConstraint = (UnaryDateConstraint) constraint;
                        if (!checkConsistency(date, dateConstraint.R_VAL, constraint)) {
                            violatedDates.add(date);
                        }
                    }
                }
            }
            meeting.domain.removeAll(violatedDates);
        }
    }

    /**
     * Checks whether the left and right date of a given constraint satisfies the
     * conditions
     * 
     * @param leftDate   LocalDate to compare
     * @param rightDate  LocalDate to compare
     * @param constraint comparison that must hold true from the comparison of two
     *                   LocalDates
     * @return boolean whether the comparison is consistent or not
     */
    private static boolean checkConsistency(LocalDate leftDate, LocalDate rightDate, DateConstraint constraint) {
        boolean isConsistent = false;

        switch (constraint.OP) {
        case "==":
            if (leftDate.isEqual(rightDate)) {
                isConsistent = true;
            }
            break;
        case "!=":
            if (!leftDate.isEqual(rightDate)) {
                isConsistent = true;
            }
            break;
        case "<":
            if (leftDate.isBefore(rightDate)) {
                isConsistent = true;
            }
            break;
        case "<=":
            if (leftDate.isBefore(rightDate) || leftDate.isEqual(rightDate)) {
                isConsistent = true;
            }
            break;
        case ">":
            if (leftDate.isAfter(rightDate)) {
                isConsistent = true;
            }
            break;
        case ">=":
            if (leftDate.isEqual(rightDate) || leftDate.isAfter(rightDate)) {
                isConsistent = true;
            }
            break;
        }
        return isConsistent;
    }


    private static Queue<Arc> generateArcSet(List<DateVar> meetings, Set<DateConstraint> constraints) {
        Queue<Arc> arcSet = new LinkedList<Arc>();
        Arc newArc = null;
        Arc newArcReversed = null;
        BinaryDateConstraint dateConstraint;
        for (DateConstraint constraint : constraints) {
            if (constraint.arity() == 2) {
                dateConstraint = (BinaryDateConstraint) constraint;

                newArc = new Arc(dateConstraint.L_VAL, dateConstraint.R_VAL);
                newArcReversed = new Arc(dateConstraint.R_VAL, dateConstraint.L_VAL);

                if (!arcSet.contains(newArc)) {
                    arcSet.add(newArc);
                }
                if (!arcSet.contains(newArcReversed)) {
                    arcSet.add(newArcReversed);
                }

            }

        }
        return arcSet;
    }

    private static Set<Arc> findTailSet(int head, List<DateVar> meetings, Set<DateConstraint> constraints) {
        Set<Arc> tailSet = new HashSet<Arc>();
        BinaryDateConstraint dateConstraint = null;

        for (DateConstraint constraint : constraints) {
            if (constraint.arity() == 2) {
                dateConstraint = (BinaryDateConstraint) constraint;
                if (dateConstraint.R_VAL == head) {
                    Arc add = new Arc(dateConstraint.L_VAL, head);
                    tailSet.add(add);
                }
                if (dateConstraint.L_VAL == head) {
                    Arc add = new Arc(dateConstraint.R_VAL, head);
                    tailSet.add(add);
                }
            }
        }

        return tailSet;
    }

    private static void ac3Preprocessing(List<DateVar> meetings, Set<DateConstraint> constraints) {
        BinaryDateConstraint dateConstraint = null;
        Set<LocalDate> violatedConstraints = new HashSet<LocalDate>();
        Queue<Arc> arcSet = generateArcSet(meetings, constraints);
        Arc toCheck;
        int count = 0; 

        while (!arcSet.isEmpty()) {
            toCheck = arcSet.poll();
            for (DateConstraint constraint : constraints) {
                if (constraint.arity() == 2) {
                    dateConstraint = (BinaryDateConstraint) constraint; 
                    for (LocalDate date : meetings.get(toCheck.head).domain) {
                        count = 0;
                        for (LocalDate dateTail : meetings.get(toCheck.tail).domain) {
                          
                            if (dateConstraint.L_VAL == toCheck.head && dateConstraint.R_VAL == toCheck.tail) {
                                if (checkConsistency(date, dateTail, constraint)) {
                                    count++;
                                }
                            } else if (dateConstraint.R_VAL == toCheck.head && dateConstraint.L_VAL == toCheck.tail) {
                                if (checkConsistency(dateTail, date, constraint)) {
                                    count++;
                                }
                            }
                        }
                        if(count == 0) {
                            violatedConstraints.add(date); 
                        }

                    }
                
                }

            }
            if(!violatedConstraints.isEmpty()) {
                meetings.get(toCheck.head).domain.removeAll(violatedConstraints); 
                Set<Arc> tailSet = findTailSet(toCheck.head, meetings, constraints);
                if (tailSet != null) {
                  for (Arc arc : tailSet) {
                      if (!arcSet.contains(arc)) {
                          arcSet.add(arc);
                      }
                  }
              }

            }
        }
    }

}
