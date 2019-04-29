package cat.flx.plataformes.engine;

// Interface for contact listeners
// Used in Scene definition
public interface OnContactListener {
    void onContact(String tag1, GameObject object1, String tag2, GameObject object2);
}
