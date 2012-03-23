package basis.environments;

public interface Environment {
	<T> T provide(Class<T> need);
}