package eu.iescities.pilot.rovereto.inbici.custom.data.model;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

public class ObjectFilter {

        private boolean myObjects;
        
        private double[] center;
        private Double radius;
        private List<String> types;
        private Long fromTime;
        private Long toTime;
         private String text;
         private SortedMap<String,Integer> sort = null;
        private Integer limit;
        private Integer skip;
         private String className;
        
        public String getClassName() {
                return className;
        }

        public void setClassName(String className) {
                this.className = className;
        }

        private String domainType;

        private Map<String,Object> criteria = null;
        
        public ObjectFilter() {
                super();
        }

        public double[] getCenter() {
                return center;
        }

        public void setCenter(double[] center) {
                this.center = center;
        }

        public Double getRadius() {
                return radius;
        }

        public void setRadius(Double  radius) {
                this.radius = radius;
        }


        public List<String> getTypes() {
                return types;
        }

        public void setTypes(List<String> types) {
                this.types = types;
        }

        public Long getFromTime() {
                return fromTime;
        }

        public void setFromTime(Long fromTime) {
                this.fromTime = fromTime;
        }

        public Long getToTime() {
                return toTime;
        }

        public void setToTime(Long toTime) {
                this.toTime = toTime;
        }

        public Integer getLimit() {
                return limit;
        }

        public void setLimit(Integer limit) {
                this.limit = limit;
        }

        public Integer getSkip() {
                return skip;
        }

        public void setSkip(Integer skip) {
                this.skip = skip;
        }

        public String getDomainType() {
                return domainType;
        }

        public void setDomainType(String domainType) {
                this.domainType = domainType;
        }

        public Map<String, Object> getCriteria() {
                return criteria;
        }

        public void setCriteria(Map<String, Object> criteria) {
                this.criteria = criteria;
        }

        public boolean isMyObjects() {
                return myObjects;
        }

        public void setMyObjects(boolean myObjects) {
                this.myObjects = myObjects;
        }

        public String getText() {
                return text;
        }

        public void setText(String text) {
                this.text = text;
        }

        public SortedMap<String, Integer> getSort() {
                return sort;
        }

        public void setSort(SortedMap<String, Integer> sort) {
                this.sort = sort;
        }
}