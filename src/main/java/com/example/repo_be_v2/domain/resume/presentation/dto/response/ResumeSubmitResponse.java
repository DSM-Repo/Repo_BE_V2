package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.enms.ResumeSubmissionStatus;
public record ResumeSubmitResponse(
        String resumeId,
        ResumeSubmissionStatus submissionStatus
){
}
