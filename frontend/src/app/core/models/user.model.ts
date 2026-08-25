export type UserStatus = 'ACTIVE' | 'INACTIVE';

export interface User {
  id: number;
  name: string;
  email: string;
  roleId: number;
  roleName: string;
  status: UserStatus;
  username?: string;
}

export interface UserRequest {
  username: string;
  name: string;
  email: string;
  password: string;
  roleId: number;
}

export interface ProfileUpdateRequest {
  name: string;
  email: string;
  password: string;
}

export interface RoleUpdateRequest {
  roleId: number;
}

export interface StatusUpdateRequest {
  status: UserStatus;
}
