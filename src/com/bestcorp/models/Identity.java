package com.bestcorp.models;

public class Identity 
{
    private int id;
    private String sptId;
    private String sptName;

    //designate default constructor???

    public Identity(int id, String sptId, String sptName)
    {
        this.id = id;
        this.sptId = sptId;
        this.sptName = sptName;
    }

    public Identity(String sptId, String sptName)
    {
        this.sptId = sptId;
        this.sptName = sptName;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSptId() {
        return sptId;
    }

    public void setSptId(String sptId) {
        this.sptId = sptId;
    }

    public String getSptName() {
        return sptName;
    }

    public void setSptName(String sptName) {
        this.sptName = sptName;
    }


    @Override
    public String toString() {
        return "Identity [id=" + id + ", sptId=" + sptId + ", sptName=" + sptName + "]";
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + ((sptId == null) ? 0 : sptId.hashCode());
        result = prime * result + ((sptName == null) ? 0 : sptName.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Identity other = (Identity) obj;
        if (id != other.id)
            return false;
        if (sptId == null) {
            if (other.sptId != null)
                return false;
        } else if (!sptId.equals(other.sptId))
            return false;
        if (sptName == null) {
            if (other.sptName != null)
                return false;
        } else if (!sptName.equals(other.sptName))
            return false;
        return true;
    }

    
    
}
