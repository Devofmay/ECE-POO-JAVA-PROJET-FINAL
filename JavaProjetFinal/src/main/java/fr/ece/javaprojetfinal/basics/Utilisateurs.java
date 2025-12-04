package main.java.fr.ece.javaprojetfinal.basics;

public class Utilisateurs {

        private int id;
        private String name;
        private String address;
        private int role;
        private String mdp;

        public Utilisateurs(int id, String name, String address, int role, String mdp) {
            this.id = id;
            this.name = name;
            this.address = address;
            this.role = role;
            this.mdp = mdp;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public int getRole() {
            return role;
        }

        public void setRole(int role) {
            this.role = role;
        }

        public String getMdp() {
            return mdp;
        }

        public void setMdp(String mdp) {
            this.mdp = mdp;
        }
    }


