package cqupt.ql.test;


import cqupt.ql.rpc.annotation.Service;
import cqupt.ql.rpc.api.ByeService;

/**
 * @author ziyang
 */
@Service
public class ByeServiceImpl implements ByeService {

    @Override
    public String bye(String name) {
        return "bye, " + name;
    }
}
