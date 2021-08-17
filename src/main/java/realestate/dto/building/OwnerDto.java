package realestate.dto.building;

import javax.validation.constraints.NotNull;

public class OwnerDto {
    @NotNull
    private Long id;

    @NotNull
    private String ownerName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
}
