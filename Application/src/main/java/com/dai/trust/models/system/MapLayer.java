package com.dai.trust.models.system;

import com.dai.trust.models.AbstractIdEntity;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "map_layer")
public class MapLayer extends AbstractIdEntity {

    @Column
    private String name;
    
    @Column
    private String title;
    
    @Column(name = "type_code")
    private String typeCode;
    
    @Column
    private boolean active;
    
    @Column(name = "layer_order")
    private int order;
    
    @Column
    private String url;
    
    @Column(name = "version")
    private String versionNum;
    
    @Column(name = "image_format")
    private String imageFormat;
    
    @Column(name = "username")
    private String userName;

    @Column
    private String passwd;

    @OneToMany(mappedBy = "mapLayer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MapLayerOption> options;
    
    public MapLayer() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersionNum() {
        return versionNum;
    }

    public void setVersionNum(String versionNum) {
        this.versionNum = versionNum;
    }

    public String getImageFormat() {
        return imageFormat;
    }

    public void setImageFormat(String imageFormat) {
        this.imageFormat = imageFormat;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public List<MapLayerOption> getOptions() {
        return options;
    }

    public void setOptions(List<MapLayerOption> options) {
        this.options = options;
    }
}
