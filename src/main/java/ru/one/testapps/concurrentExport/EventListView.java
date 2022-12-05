package ru.one.testapps.concurrentExport;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@Setter
public class EventListView {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeFirstname;
    private LocalDate beginDate;
    private Long subdivisionId;
    private String subdivisionName;
    private String subdivisionCode;
    private Long typeId;
    private String typeName;
    private LocalDateTime eventDate;
    private Long statusId;
    private String statusName;
    private Long certifierId;
    private String certifierName;
    private Long hrId;
    private String hrName;
    private Long lmId;
    private String lmName;
    private Long reconId;
    private String reconName;
    private Long assertId;
    private String assertName;
    private Long[] otherId;
    private String[] otherName;
    private String prevPosition;
    private String prevGrade;
    private String prevSubGrade;
    private BigDecimal prevSalary;
    private String nextPosition;
    private String nextGrade;
    private String nextSubGrade;
    private BigDecimal nextSalary;
    private String location;
    private String company;
    private LocalDateTime prevDate;
    private Long alId;

    public EventListView() {
    }
}