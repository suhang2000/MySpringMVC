import lsh.core.annotation.Service;

@Service
public class TestServiceImpl implements TestService{
    @Override
    public String helloWorld() {
        return "hello world";
    }
}
