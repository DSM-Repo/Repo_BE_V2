package com.example.repo_be_v2.domain.resume.presentation.dto.response;

import com.example.repo_be_v2.domain.resume.domain.enums.ResumeSubmissionStatus;
import java.util.List;

public record ResumeSubmitResponse(
        String resumeId,
        ResumeSubmissionStatus submissionStatus,
        List<ResumePageIdResponse> pages
){
}
