package academy.wakanda.wakanda_ai.jornadawakander.application.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@ToString
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class MemberkitUserDto {
    private Long id;

    @JsonProperty("full_name")
    private String fullName;

    private String email;
    private String bio;

    @JsonProperty("profile_image_url")
    private String profileImageUrl;

    private boolean blocked;
    private boolean unlimited;

    @JsonProperty("sign_in_count")
    private Integer signInCount;
    
    @JsonProperty("current_sign_in_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS[xxx]", shape = JsonFormat.Shape.STRING)
    private LocalDateTime currentSignInAt;
    
    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS[xxx]", shape = JsonFormat.Shape.STRING)
    private LocalDateTime createdAt;
    
    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS[xxx]", shape = JsonFormat.Shape.STRING)
    private LocalDateTime updatedAt;
    
    private Metadata metadata;
    private List<Enrollment> enrollments;
    private List<Membership> memberships;

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Metadata {
        @JsonProperty("cpf_cnpj")
        private String cpfCnpj;

        @JsonProperty("phone_local_code")
        private String phoneLocalCode;

        @JsonProperty("phone_number")
        private String phoneNumber;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Enrollment {
        private Long id;
        private String status;
        
        @JsonProperty("course_id")
        private Long courseId;
        
        @JsonProperty("classroom_id")
        private Long classroomId;
        
        @JsonProperty("expire_date")
        private String expireDate;
    }

    @Getter
    @ToString
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Membership {
        private Long id;
        private String status;
        
        @JsonProperty("membership_level_id")
        private Long membershipLevelId;
        
        @JsonProperty("expire_date")
        private String expireDate;
    }
}
