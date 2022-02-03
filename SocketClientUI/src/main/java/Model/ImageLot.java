package Model;

public class ImageLot {
    private String image_name;
    private int image_size;
    private String path_image;

    public ImageLot(String image_name, int image_size, String path_image) {
        this.image_name = image_name;
        this.image_size = image_size;
        this.path_image = path_image;
    }

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public int getImage_size() {
        return image_size;
    }

    public void setImage_size(int image_size) {
        this.image_size = image_size;
    }

    public String getPath_image() {
        return path_image;
    }

    public void setPath_image(String path_image) {
        this.path_image = path_image;
    }

    @Override
    public String toString() {
        return "ImageLot{" +
                "image_name='" + image_name + '\'' +
                ", image_size=" + image_size +
                ", path_image='" + path_image + '\'' +
                '}';
    }
}
