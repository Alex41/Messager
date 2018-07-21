package ua.alex.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString(exclude = "desk")
public class SuccessResponse {
    final Integer error;
    final Integer success;
    final String desk;

    public SuccessResponse(Integer error, Integer success) {
        this.error = error;
        this.success = success;
        this.desk = null;
    }

    public static SuccessResponse userNonMember(){
        return new SuccessResponse(1, 0, "USER IS NON MEMBER OF CHAT OR CHAT PUBLIC");
    }

    public static SuccessResponse emptyMessage(){
        return new SuccessResponse(1,0,"MESSAGE CAN NOT BE EMPTY");
    }

    public static SuccessResponse ok(){
        return new SuccessResponse(0,1, null);
    }

    public static SuccessResponse userIsAlreadyExist(){
        return new SuccessResponse(2, 0, "USER IS ALREADY EXIST");
    }

}
