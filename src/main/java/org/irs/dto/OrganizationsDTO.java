package org.irs.dto;

public class OrganizationsDTO {
    public String id;
    public String label;
    public ImageDTO image;
    public String description;
    public String phone;
    public String location;
    

    public OrganizationsDTO(String id, String description, ImageDTO image_uri, String label, String location, String phone) {
        this.id = id;
        this.description = description;
        this.image = image_uri;
        this.label = label;
        this.location = location;
        this.phone = phone;
    }


    @Override
    public String toString() {
        return "OrganizationsDTO [id=" + id + ", label=" + label + ", image_uri=" + image + ", description=" + description
                + ", phone=" + phone + ", location=" + location + "]";
    }

    
}
