export class CurrentUserDto {
    objectId?: number;
    currentUserId?: number = Number(sessionStorage.getItem('userID'));

}
