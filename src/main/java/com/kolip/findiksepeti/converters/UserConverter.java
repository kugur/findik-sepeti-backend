package com.kolip.findiksepeti.converters;

import com.kolip.findiksepeti.user.CustomUser;
import com.kolip.findiksepeti.user.UserDto;
import org.springframework.stereotype.Service;

@Service
public class UserConverter implements Converter<UserDto, CustomUser> {

    @Override
    public CustomUser convert(UserDto beConverted) {
        CustomUser user = new CustomUser();
        user.setEmail(beConverted.getUsername());
        return convert(beConverted, user);
    }

    @Override
    public CustomUser convert(UserDto beConverted, CustomUser beMerged) {
        beMerged.setFirstName(beConverted.getFirstName());
        beMerged.setLastName(beConverted.getLastName());
        beMerged.setAddress(beConverted.getAddress());
        beMerged.setGender(beConverted.getGender());

        return beMerged;
    }
}
