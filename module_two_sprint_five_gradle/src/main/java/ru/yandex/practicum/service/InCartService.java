package ru.yandex.practicum.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.dto.ItemDto;

import ru.yandex.practicum.mapping.ItemEntityMapper;

import ru.yandex.practicum.model.InCart;
import ru.yandex.practicum.model.Item;
import ru.yandex.practicum.model.User;
import ru.yandex.practicum.repository.InCartRepository;
import ru.yandex.practicum.repository.ItemRepository;
import ru.yandex.practicum.repository.UserRepository;

@Service
public class InCartService {


    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final InCartRepository inCartRepository;

    public InCartService(ItemRepository itemRepository, UserRepository userRepository, InCartRepository inCartRepository){
        this.itemRepository = itemRepository;
        this.userRepository = userRepository;
        this.inCartRepository = inCartRepository;
    }

    public void changeInCardCount(long id, int command) {
        InCart inCart = inCartRepository.findByItem_IdAndUser_Id(id,1L);
        User user = userRepository.findById(1).orElse(null);
        Item item = itemRepository.findById((int)id).orElse(null);
        if (command == 1) {
            if (inCart ==null) { inCart =new InCart();
                inCart.setItem(item);
                inCart.setUser(user);
                inCart.setCount(0l);
                inCartRepository.save(inCart);
            }
            inCart.setCount(inCart.getCount()+1);
            inCartRepository.save(inCart);
        } else if (command ==2) {
                    if ((inCart !=null)&&(inCart.getCount()>0)) {
                        inCart.setCount(inCart.getCount()-1);
                        inCartRepository.save(inCart);
                        if (inCart.getCount()==0)  { inCartRepository.delete(inCart); }
                    }
               } else if (command ==3) { inCartRepository.delete(inCart); }
    }
}
